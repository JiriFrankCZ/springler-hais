package eu.jirifrank.springler.service.realtime;

import eu.jirifrank.springler.api.entity.Irrigation;
import eu.jirifrank.springler.api.entity.SensorRead;
import eu.jirifrank.springler.api.enums.Location;
import eu.jirifrank.springler.api.enums.SensorType;
import eu.jirifrank.springler.api.model.watering.ScoredIrrigation;
import eu.jirifrank.springler.service.communication.CommunicationService;
import eu.jirifrank.springler.service.persistence.IrrigationRepository;
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
    private static final double SOIL_MOISTURE_WEIGHT = 0.9;
    private static final double TEMPERATURE_WEIGHT = 0.7;
    private static final double HUMIDITY_WEIGHT = 0.5;

    @Value("${watering.soil.moisture.ideal}")
    private Double soilMoistureIdeal;

    @Value("${watering.soil.moisture.threshold}")
    private Double soilMoistureThreshold;

    @Value("${watering.duration.default}")
    private Double defaultWateringDuration;

    private SensorRead humidity;

    private SensorRead soilMoisture;

    private SensorRead temperature;

    @Autowired
    private IrrigationRepository irrigationRepository;

    @Autowired
    private TaskScheduler taskScheduler;

    @Autowired
    private CommunicationService communicationService;

    @Autowired
    private RealtimeWeatherService weatherService;

    @Override
    public void doWatering(long duration) {

        // communicationService.writeAction(new Action(IOTAction.WATER, new WateringData(duration)));
    }

    @Scheduled(fixedDelay = 15 * 60 * 1000)
    public void reloadMeasurement() {

    }

    @Scheduled(fixedDelay = 30 * 60 * 1000)
    public void wateringCheck() {
        if (!weatherService.isRainPredicted() || soilMoisture.getValue() < (soilMoistureIdeal - soilMoistureThreshold)) {
            LOCATIONS.forEach(location -> {
                Optional<Irrigation> similarIrrigation = findSimilarOrLast(location);
                Irrigation irrigation = Irrigation.builder()
                        .date(new Date())
                        .location(location)
                        .sensorReads(Arrays.asList(humidity, soilMoisture, temperature))
                        .duration(10.0)
                        .build();

                similarIrrigation.ifPresent(irrigationPast -> {
                    final double correction = irrigationPast.getCorrection() != null ? irrigationPast.getCorrection() : 0;
                    final double duration = irrigationPast.getDuration() + correction;
                    irrigation.setDuration(duration);
                });

                irrigationRepository.save(irrigation);

                taskScheduler.schedule(() -> backpropagateResults(irrigation), Instant.now().plus(20l, MINUTES));
            });
        } else {
            log.info("Expected rain bypassed watering.");
        }
    }

    private void backpropagateResults(Irrigation irrigation) {
        log.info("Evaluating efficiency of irrigation {}.", irrigation);

        final double topBoundary = soilMoistureIdeal + soilMoistureThreshold;
        final double bottomBoundary = soilMoistureIdeal - soilMoistureThreshold;


        double correctionCoefficient = 0.0;

        double soilMositureValue = soilMoisture.getValue();
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

        log.info("Evaluation finished. Correction has been set to {}s.", irrigation.getCorrection());
    }

    private Optional<ScoredIrrigation> findSimilarOrLast(Location location) {
        List<Irrigation> irrigationList = irrigationRepository.findByMonthAndLocation(location);

        return Optional.ofNullable(irrigationList.stream()
                .map(irrigation -> new ScoredIrrigation(irrigation, calculateScore(irrigation)))
                .filter(irrigation -> irrigation.getScore() > 30)
                .sorted(Comparator.comparing().reversed())
                .findFirst()
                .orElse(new ScoredIrrigation(irrigationRepository.findFirstByLocationOrderByDateDesc(location), null));
    }

    private Double calculateScore(Irrigation irrigation) {
        final Double[] score = new Double[1];
        score[0] = 0.0;

        irrigation.getSensorReads().stream()
                .filter(sensorRead -> sensorRead.getSensorType().equals(SensorType.SOIL_MOISTURE))
                .findFirst()
                .ifPresent(sensorRead -> score[0] += SOIL_MOISTURE_WEIGHT * Math.abs(sensorRead.getValue() - soilMoisture.getValue()));

        irrigation.getSensorReads().stream()
                .filter(sensorRead -> sensorRead.getSensorType().equals(SensorType.TEMPERATURE))
                .findFirst()
                .ifPresent(sensorRead -> score[0] += TEMPERATURE_WEIGHT * Math.abs(sensorRead.getValue() - temperature.getValue()));

        irrigation.getSensorReads().stream()
                .filter(sensorRead -> sensorRead.getSensorType().equals(SensorType.HUMIDITY))
                .findFirst()
                .ifPresent(sensorRead -> score[0] += HUMIDITY_WEIGHT * Math.abs(sensorRead.getValue() - humidity.getValue()));

        log.debug("Irrigation {} has score: {}.", irrigation.toString(), score[0]);

        return score[0];
    }
}
