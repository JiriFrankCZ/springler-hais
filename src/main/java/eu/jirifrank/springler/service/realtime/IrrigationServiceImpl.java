package eu.jirifrank.springler.service.realtime;

import eu.jirifrank.springler.api.action.Action;
import eu.jirifrank.springler.api.action.WateringData;
import eu.jirifrank.springler.api.entity.Irrigation;
import eu.jirifrank.springler.api.entity.SensorRead;
import eu.jirifrank.springler.api.enums.DeviceAction;
import eu.jirifrank.springler.api.enums.Location;
import eu.jirifrank.springler.api.enums.SensorType;
import eu.jirifrank.springler.api.enums.ServiceType;
import eu.jirifrank.springler.api.model.watering.ScoredIrrigation;
import eu.jirifrank.springler.service.communication.CommunicationService;
import eu.jirifrank.springler.service.logging.LoggingService;
import eu.jirifrank.springler.service.persistence.IrrigationRepository;
import eu.jirifrank.springler.service.persistence.SensorReadRepository;
import eu.jirifrank.springler.util.NumberUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yaml.snakeyaml.util.ArrayUtils;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    private static final double HUMIDITY_WEIGHT = 0.05;
    private static final double RAIN_PROBABILITY_WEIGHT = 0.3;
    private static final double TEMPERATURE_FORECAST_WEIGHT = 0.2;
    private static final double DURATION_WEIGHT = 0.05;

    @Value("${watering.soil.moisture.ideal}")
    private Double soilMoistureIdeal;

    @Value("${watering.soil.moisture.threshold}")
    private Double soilMoistureThreshold;

    @Value("${watering.duration.default}")
    private Double defaultWateringDuration;

    private List<SensorRead> humidityList = new ArrayList<>();

    private List<SensorRead> soilMoistureList = new ArrayList<>();

    private List<SensorRead> temperatureList = new ArrayList<>();

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

        loggingService.log("Watering on demand for " + location + " has been scheduled.", ServiceType.IRRIGATION);
    }

    @Scheduled(fixedDelay = 4 * 60 * 1000)
    @Transactional(readOnly = true)
    public void reloadMeasurement() {
        log.debug("Reloading actual sensor readings per location.");
        humidityList = sensorReadRepository.findLatestByType(SensorType.HUMIDITY, ServiceType.IRRIGATION);
        temperatureList = sensorReadRepository.findLatestByType(SensorType.TEMPERATURE, ServiceType.IRRIGATION);
        soilMoistureList = sensorReadRepository.findLatestByType(SensorType.SOIL_MOISTURE, ServiceType.IRRIGATION);
    }

    @Scheduled(fixedDelay = 15 * 60 * 1000)
    @Transactional
    public void wateringCheck() {
        LOCATIONS.forEach(location -> {
            Optional<SensorRead> soilMoistureSensorReadOpt = filterSensorReadByLocation(soilMoistureList, location);
            if (!weatherService.isRainPredicted() && soilMoistureSensorReadOpt.isPresent() && soilMoistureSensorReadOpt.get().getValue() < (soilMoistureIdeal - soilMoistureThreshold)) {
                Optional<ScoredIrrigation> similarIrrigation = findSimilarOrLast(location);

                final Irrigation irrigation;

                List<SensorRead> sensorReadList = getSensorReads(location);
                if (similarIrrigation.isPresent() && similarIrrigation.get().getScore() != null) {
                    log.debug("Perfect match for irrigation.");
                    irrigation = similarIrrigation.get().getIrrigation();
                    irrigation.setUpdated(new Date());
                    irrigation.setDuration(NumberUtils.roundToHalf(irrigation.getDuration() + irrigation.getCorrection()));
                    irrigation.setCorrection(null);
                    irrigation.setIteration(irrigation.getIteration() + 1);
                 } else if(similarIrrigation.isPresent()){
                    log.debug("No similar irrigation found for given combination, starting with best available irrigation.");
                    irrigation = Irrigation.builder()
                            .created(new Date())
                            .location(location)
                            .iteration(1)
                            .sensorReads(sensorReadList)
                            .temperatureForecast(weatherService.getForecastedTemperature())
                            .rainProbability(weatherService.getRainProbability())
                            .build();

                    similarIrrigation.ifPresent(scoredIrrigationPast -> {
                        Irrigation irrigationPast = scoredIrrigationPast.getIrrigation();
                        irrigation.setDuration(irrigationPast.getDuration());
                        if (irrigationPast.getCorrection() != null) {
                            irrigation.setDuration(NumberUtils.roundToHalf(irrigation.getDuration() + irrigationPast.getCorrection()));
                        }
                    });
                } else {
                    log.debug("Not similar irrigation found. Starting for given combination from scratch.");
                    irrigation = Irrigation.builder()
                            .created(new Date())
                            .location(location)
                            .iteration(1)
                            .sensorReads(sensorReadList)
                            .temperatureForecast(weatherService.getForecastedTemperature())
                            .rainProbability(weatherService.getRainProbability())
                            .duration(5.0)
                            .build();

                }

                irrigationRepository.save(irrigation);

                WateringData wateringData = new WateringData(irrigation.getDuration(), irrigation.getLocation());
                Action action = new Action(DeviceAction.WATER, wateringData);
                communicationService.sendActionMessage(action);

                taskScheduler.schedule(() -> this.backpropagateResults(irrigation), Instant.now().plus(10l, MINUTES));

                log.info("Scheduled watering {} and submitted for processing.", wateringData);
                loggingService.log(
                        "Watering for " + location + " was scheduled with duration " + irrigation.getDuration() + "s.",
                        ServiceType.IRRIGATION
                );
            } else if (weatherService.isRainPredicted()) {
                log.info("Expected rain bypassed watering.");
                loggingService.log(
                        "Watering for " + location + " needed, but expected rain ["
                                + weatherService.getRainProbability() + "] bypassed watering.",
                        ServiceType.IRRIGATION
                );
            } else {
                log.info("Soil moisture in location {} is high enough, watering not needed.", location);
                loggingService.log("Soil moisture in location " + location + " is high enough, watering not needed.",
                        ServiceType.IRRIGATION
                );
            }
        });
    }

    private List<SensorRead> getSensorReads(Location location) {
        return Stream.of(
                filterSensorReadByLocation(humidityList, location).get(),
                filterSensorReadByLocation(soilMoistureList, location).get(),
                filterSensorReadByLocation(temperatureList, location).get()
        ).filter(Objects::nonNull).collect(Collectors.toList());
    }

    @Transactional
    protected void backpropagateResults(Irrigation irrigation) {
        log.info("Evaluating efficiency of irrigation {}.", irrigation);

        final double topBoundary = soilMoistureIdeal + soilMoistureThreshold;
        final double bottomBoundary = soilMoistureIdeal - soilMoistureThreshold;

        double correctionCoefficient = 0.0;

        double soilMoistureValue = filterSensorReadByLocation(soilMoistureList, irrigation.getLocation()).get().getValue();
        if (soilMoistureValue > topBoundary) {
            correctionCoefficient = topBoundary / soilMoistureValue;
        } else if (soilMoistureValue < bottomBoundary) {
            correctionCoefficient = bottomBoundary / soilMoistureValue;
        } else if (soilMoistureValue < topBoundary && soilMoistureValue > soilMoistureIdeal) {
            correctionCoefficient = soilMoistureIdeal / soilMoistureValue;
        } else if (soilMoistureValue > bottomBoundary && soilMoistureValue < soilMoistureIdeal) {
            correctionCoefficient = soilMoistureIdeal / soilMoistureValue;
        }

        double correction = NumberUtils.roundToHalf((irrigation.getDuration() * correctionCoefficient) - irrigation.getDuration());
        irrigation.setCorrection(correction);

        irrigationRepository.save(irrigation);

        log.info("Evaluation finished. Irrigation {} correction has been set to {}s.", irrigation.getId(), irrigation.getCorrection());
        loggingService.log("Learning by backpropagation of irrigation[" + irrigation.getId() + "] was updated by " +
                        irrigation.getCorrection() + ".",
                ServiceType.IRRIGATION
        );
    }

    private Optional<ScoredIrrigation> findSimilarOrLast(Location location) {
        List<Irrigation> irrigationList = irrigationRepository.findByMonthAndLocation(location);

        Optional<ScoredIrrigation> chosenIrrigation = irrigationList.stream()
                .map(irrigation -> new ScoredIrrigation(irrigation, calculateScore(irrigation)))
                .filter(irrigation -> irrigation.getScore() < 15)
                .sorted(Comparator.comparing(ScoredIrrigation::getScore).reversed())
                .findFirst();

        if (chosenIrrigation.isPresent()) {
            return chosenIrrigation;
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
                    SensorRead sensorReadActual = filterSensorReadByLocation(soilMoistureList, sensorRead.getLocation()).get();
                    score[0] += SOIL_MOISTURE_WEIGHT * Math.abs(sensorRead.getValue() - sensorReadActual.getValue());
                });

        irrigation.getSensorReads().stream()
                .filter(sensorRead -> sensorRead.getSensorType().equals(SensorType.TEMPERATURE))
                .findFirst()
                .ifPresent(sensorRead -> {
                    SensorRead sensorReadActual = filterSensorReadByLocation(temperatureList, sensorRead.getLocation()).get();
                    score[0] += TEMPERATURE_WEIGHT * Math.abs(sensorRead.getValue() - sensorReadActual.getValue());
                });

        irrigation.getSensorReads().stream()
                .filter(sensorRead -> sensorRead.getSensorType().equals(SensorType.HUMIDITY))
                .findFirst()
                .ifPresent(sensorRead -> {
                    SensorRead sensorReadActual = filterSensorReadByLocation(humidityList, sensorRead.getLocation()).get();
                    score[0] += HUMIDITY_WEIGHT * Math.abs(sensorRead.getValue() - sensorReadActual.getValue());
                });


        score[0] += RAIN_PROBABILITY_WEIGHT * Math.abs(irrigation.getRainProbability() - weatherService.getRainProbability());

        score[0] += TEMPERATURE_FORECAST_WEIGHT * Math.abs(irrigation.getTemperatureForecast() - weatherService.getForecastedTemperature());

        score[0] += DURATION_WEIGHT * irrigation.getDuration();

        log.debug("Irrigation {} has score: {}.", irrigation.getId(), score[0]);

        return score[0];
    }

    private Optional<SensorRead> filterSensorReadByLocation(List<SensorRead> sensorReadList, Location location) {
        if (sensorReadList.isEmpty()) {
            return Optional.empty();
        }

        return sensorReadList.stream()
                .filter(sensorRead -> sensorRead.getLocation().equals(location) || sensorRead.getLocation().equals(Location.ALL))
                .findFirst();
    }
}
