package eu.jirifrank.springler.service.realtime;

import eu.jirifrank.springler.api.entity.Irrigation;
import eu.jirifrank.springler.api.entity.SensorRead;
import eu.jirifrank.springler.api.enums.Location;
import eu.jirifrank.springler.api.enums.SensorType;
import eu.jirifrank.springler.service.communication.CommunicationService;
import eu.jirifrank.springler.service.persistence.IrrigationRepository;
import eu.jirifrank.springler.service.persistence.SensorReadRepository;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.util.ArrayUtils;

import java.time.Instant;
import java.time.temporal.TemporalAmount;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static java.time.temporal.ChronoUnit.HOURS;

@Service
@Slf4j
public class IrrigationServiceImpl implements IrrigationService {

    private static final List<Location> LOCATIONS = ArrayUtils.toUnmodifiableList(
            Location.OPENED,
            Location.COVERED
    );

    @Autowired
    private CommunicationService communicationService;

    @Autowired
    private RealtimeWeatherService weatherService;

    @Value("${watering.soil.moisture.ideal}")
    private Double soilMoistureIdeal;

    @Value("${watering.soil.moisture.threshold}")
    private Double soilMoistureThreshold;

    private Double humidity = 50.00;

    private Double soilMoisture = 50.00;

    private Double temperature = 20.00;

    @Autowired
    private IrrigationRepository irrigationRepository;

    @Autowired
    private TaskScheduler taskScheduler;

    @Override
    public void doWatering(long duration) {

        // communicationService.writeAction(new Action(IOTAction.WATER, new WateringData(duration)));
    }

    @Scheduled(fixedDelay = 15 * 60 * 1000)
    public void reloadMeasurement() {

    }

    @Scheduled(fixedDelay = 15 * 60 * 1000)
    public void wateringCheck() {
        if(!weatherService.isRainPredicted()){
            LOCATIONS.forEach(location -> {
                Optional<Irrigation> similarIrrigation = findSimilar(location);
                Irrigation irrigation = Irrigation.builder()
                        .date(new Date())
                        .location(location)
                        .build();

                similarIrrigation.ifPresent(irrigationPast -> irrigation.setDuration(irrigation.getDuration() + irrigation.getCorrection()));

                taskScheduler.schedule(() -> learnLesson(irrigation), Instant.now().plus(1l, HOURS));
            });
        }
    }

    private void learnLesson(Irrigation irrigation) {
        log.info("Evaluating effectivity of irrigation {}.", irrigation);
        if(soilMoisture > (soilMoistureIdeal + soilMoistureThreshold)){
            log.info("Decrease irrigation length.");
        } else if(soilMoisture > (soilMoistureIdeal - soilMoistureThreshold)){
            log.info("Increase irrigation length.");
        }else{
            log.info("Irrigation was efficient.");
        }
        log.info("Evaluation finished.");
    }

    private Optional<Irrigation> findSimilar(Location location){
        List<Irrigation> irrigationList = irrigationRepository.findByMonthAndLocation(location);

        return Optional.ofNullable(irrigationList.stream()
                .map(irrigation -> {
                    irrigation.setScore(calculateScore(irrigation));
                    return irrigation;
                })
                .filter(irrigation -> irrigation.getScore() > 30)
                .sorted(Comparator.comparing(Irrigation::getScore))
                .findFirst()
                .orElse(irrigationRepository.findFirstByLocationOrderByDateDesc(location)));
    }

    private Double calculateScore(Irrigation irrigation){
        final Double[] score = new Double[1];
        score[0] = 0.0;

        irrigation.getSensorReads().stream()
                .filter(sensorRead -> sensorRead.getSensorType().equals(SensorType.SOIL_MOISTURE))
                .findFirst()
                .ifPresent(sensorRead ->  score[0] += Math.abs(sensorRead.getValue() - soilMoisture));

        irrigation.getSensorReads().stream()
                .filter(sensorRead -> sensorRead.getSensorType().equals(SensorType.TEMPERATURE))
                .findFirst()
                .ifPresent(sensorRead ->  score[0] += Math.abs(sensorRead.getValue() - temperature));

        irrigation.getSensorReads().stream()
                .filter(sensorRead -> sensorRead.getSensorType().equals(SensorType.HUMIDITY))
                .findFirst()
                .ifPresent(sensorRead ->  score[0] += Math.abs(sensorRead.getValue() - humidity));

        log.debug("Irrigation {} has score: {}.", irrigation.toString(), score[0]);

        return score[0];
    }
}
