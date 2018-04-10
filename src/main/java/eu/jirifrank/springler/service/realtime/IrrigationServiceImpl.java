package eu.jirifrank.springler.service.realtime;

import eu.jirifrank.springler.api.action.Action;
import eu.jirifrank.springler.api.action.WateringData;
import eu.jirifrank.springler.api.entity.Irrigation;
import eu.jirifrank.springler.api.entity.SensorRead;
import eu.jirifrank.springler.api.enums.DeviceAction;
import eu.jirifrank.springler.api.enums.Location;
import eu.jirifrank.springler.api.enums.SensorType;
import eu.jirifrank.springler.api.model.watering.ScoredIrrigation;
import eu.jirifrank.springler.service.communication.CommunicationService;
import eu.jirifrank.springler.service.logging.LoggingService;
import eu.jirifrank.springler.service.persistence.IrrigationRepository;
import eu.jirifrank.springler.service.persistence.SensorReadRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.util.ArrayUtils;

import java.time.Instant;
import java.util.*;

import static java.time.temporal.ChronoUnit.MINUTES;

@Service
@Slf4j
public class IrrigationServiceImpl implements IrrigationService {

    private static final List<Location> LOCATIONS = ArrayUtils.toUnmodifiableList(
            Location.OPENED,
            Location.COVERED
    );
    private static final double SOIL_MOISTURE_WEIGHT = 0.3;
    private static final double TEMPERATURE_WEIGHT = 0.1;
    private static final double HUMIDITY_WEIGHT = 0.1;
    private static final double RAIN_PROBABILITY_WEIGHT = 0.3;
    private static final double TEMPERATURE_FORECAST_WEIGHT = 0.2;

    @Value("${watering.soil.moisture.ideal}")
    private Double soilMoistureIdeal;

    @Value("${watering.soil.moisture.threshold}")
    private Double soilMoistureThreshold;

    @Value("${watering.duration.default}")
    private Double defaultWateringDuration;

    private List<SensorRead> humidityList;

    private List<SensorRead> soilMoistureList;

    private List<SensorRead> temperatureList;

    @Autowired
    private IrrigationRepository irrigationRepository;

    @Autowired
    private SensorReadRepository sensorReadRepository;

    @Autowired
    private TaskScheduler taskScheduler;

    @Autowired
    private CommunicationService communicationService;

    @Autowired
    private RealtimeWeatherService weatherService;

    @Autowired
    private LoggingService loggingService;

    @Override
    public void doWatering(Double duration, Location location) {
        log.debug("Manual watering triggered for location {} and duration {}s.", location, duration);

        communicationService.sendActionMessage(new Action(DeviceAction.WATER, new WateringData(duration, location)));

        loggingService.log("Watering on demand for " + location + "has been scheduled.");
    }

    @Scheduled(fixedDelay = 1 * 60 * 1000)
    public void reloadMeasurement() {
        log.debug("Reloading actual sensor readings per location.");
        humidityList = sensorReadRepository.findLatestByType(SensorType.HUMIDITY);
        temperatureList = sensorReadRepository.findLatestByType(SensorType.TEMPERATURE);
        soilMoistureList = sensorReadRepository.findLatestByType(SensorType.SOIL_MOISTURE);
    }

    @Scheduled(fixedDelay = 15 * 60 * 1000)
    public void wateringCheck() {
        LOCATIONS.forEach(location -> {
            if (!weatherService.isRainPredicted() || filterSensorReadByLocation(soilMoistureList, location).getValue() < (soilMoistureIdeal - soilMoistureThreshold)) {
                Optional<ScoredIrrigation> similarIrrigation = findSimilarOrLast(location);

                Irrigation irrigation;

                if (similarIrrigation.isPresent() && similarIrrigation.get().getScore() != null) {
                    log.debug("Perfect match for irrigation.");
                    irrigation = similarIrrigation.get().getIrrigation();
                    irrigation.setUpdated(new Date());
                    irrigation.setDuration(irrigation.getDuration() + irrigation.getCorrection());
                 } else {
                    log.debug("Not similar irrigation found. Starting for given combination from scratch.");
                    irrigation = Irrigation.builder()
                            .created(new Date())
                            .location(location)
                            .sensorReads(getSensorReads(location))
                            .temperatureForecast(weatherService.getForecastedTemperature())
                            .rainProbability(weatherService.getRainProbability())
                            .duration(1.0)
                            .build();

                    similarIrrigation.ifPresent(scoredIrrigationPast -> {
                        Irrigation irrigationPast = scoredIrrigationPast.getIrrigation();
                        irrigation.setDuration(irrigationPast.getDuration() + irrigationPast.getCorrection());
                    });
                }

                irrigationRepository.save(irrigation);

                WateringData wateringData = new WateringData(irrigation.getDuration(), irrigation.getLocation());
                Action action = new Action(DeviceAction.WATER, wateringData);
                communicationService.sendActionMessage(action);

                taskScheduler.schedule(() -> backpropagateResults(irrigation), Instant.now().plus(10l, MINUTES));

                log.info("Scheduled watering {} and submitted for processing.", wateringData);
                loggingService.log("Watering for " + location + " was scheduled with duration " + irrigation.getDuration() + "s.");
            } else {
                log.info("Expected rain bypassed watering.");
                loggingService.log("Watering for " + location + " needed, but expected rain ["
                        + weatherService.getRainProbability() + "] bypassed watering."
                );
            }
        });
    }

    private List<SensorRead> getSensorReads(Location location) {
        return Arrays.asList(
                filterSensorReadByLocation(humidityList, location),
                filterSensorReadByLocation(soilMoistureList, location),
                filterSensorReadByLocation(temperatureList, location)
        );
    }

    private void backpropagateResults(Irrigation irrigation) {
        log.info("Evaluating efficiency of irrigation {}.", irrigation);

        final double topBoundary = soilMoistureIdeal + soilMoistureThreshold;
        final double bottomBoundary = soilMoistureIdeal - soilMoistureThreshold;

        double correctionCoefficient = 0.0;

        double soilMositureValue = filterSensorReadByLocation(soilMoistureList, irrigation.getLocation()).getValue();
        if (soilMositureValue > topBoundary) {
            correctionCoefficient = topBoundary / soilMositureValue;
        } else if (soilMositureValue < bottomBoundary) {
            correctionCoefficient = bottomBoundary / soilMositureValue;
        } else if (soilMositureValue < topBoundary && soilMositureValue > soilMoistureIdeal) {
            correctionCoefficient = soilMoistureIdeal / soilMositureValue;
        } else if (soilMositureValue > bottomBoundary && soilMositureValue < soilMoistureIdeal) {
            correctionCoefficient = soilMoistureIdeal / soilMositureValue;
        }

        double correction = (irrigation.getDuration() * correctionCoefficient) - irrigation.getDuration();
        irrigation.setCorrection(correction);

        irrigationRepository.save(irrigation);

        log.info("Evaluation finished. Irrigation {} correction has been set to {}s.", irrigation, irrigation.getCorrection());
        loggingService.log("Learning by backpropagation of irrigation[" + irrigation.getId() + "] was updated by " +
                irrigation.getCorrection() + "."
        );
    }

    private Optional<ScoredIrrigation> findSimilarOrLast(Location location) {
        List<Irrigation> irrigationList = irrigationRepository.findByMonthAndLocation(location);

        Optional<ScoredIrrigation> choosenIrrigation = irrigationList.stream()
                .map(irrigation -> new ScoredIrrigation(irrigation, calculateScore(irrigation)))
                .filter(irrigation -> irrigation.getScore() < 30)
                .sorted(Comparator.comparing(ScoredIrrigation::getScore).reversed())
                .findFirst();

        if (choosenIrrigation.isPresent()) {
            return choosenIrrigation;
        } else {
            Irrigation irrigation = irrigationRepository.findFirstByLocationOrderByCreatedDesc(location);
            if (irrigation == null) {
                return Optional.empty();
            } else {
                return Optional.of(new ScoredIrrigation(irrigation, null));
            }
        }
    }

    private Double calculateScore(Irrigation irrigation) {
        final Double[] score = new Double[1];
        score[0] = 0.0;

        irrigation.getSensorReads().stream()
                .filter(sensorRead -> sensorRead.getSensorType().equals(SensorType.SOIL_MOISTURE))
                .findFirst()
                .ifPresent(sensorRead -> {
                    SensorRead sensorReadActual = filterSensorReadByLocation(soilMoistureList, sensorRead.getLocation());
                    score[0] += SOIL_MOISTURE_WEIGHT * Math.abs(sensorRead.getValue() - sensorReadActual.getValue());
                });

        irrigation.getSensorReads().stream()
                .filter(sensorRead -> sensorRead.getSensorType().equals(SensorType.TEMPERATURE))
                .findFirst()
                .ifPresent(sensorRead -> {
                    SensorRead sensorReadActual = filterSensorReadByLocation(temperatureList, sensorRead.getLocation());
                    score[0] += TEMPERATURE_WEIGHT * Math.abs(sensorRead.getValue() - sensorReadActual.getValue());
                });

        irrigation.getSensorReads().stream()
                .filter(sensorRead -> sensorRead.getSensorType().equals(SensorType.HUMIDITY))
                .findFirst()
                .ifPresent(sensorRead -> {
                    SensorRead sensorReadActual = filterSensorReadByLocation(humidityList, sensorRead.getLocation());
                    score[0] += HUMIDITY_WEIGHT * Math.abs(sensorRead.getValue() - sensorReadActual.getValue());
                });


        score[0] += RAIN_PROBABILITY_WEIGHT * Math.abs(irrigation.getRainProbability() - weatherService.getRainProbability());

        score[0] += TEMPERATURE_FORECAST_WEIGHT * Math.abs(irrigation.getTemperatureForecast() - weatherService.getForecastedTemperature());

        log.debug("Irrigation {} has score: {}.", irrigation.toString(), score[0]);

        return score[0];
    }

    private SensorRead filterSensorReadByLocation(List<SensorRead> sensorReadList, Location location) {
        if (sensorReadList.isEmpty()) {
            return null;
        }

        return sensorReadList.stream()
                .filter(sensorRead -> sensorRead.getLocation().equals(location))
                .findFirst()
                .get();
    }
}
