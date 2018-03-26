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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class IrrigationServiceImpl implements IrrigationService {

    @Autowired
    private CommunicationService communicationService;

    @Autowired
    private RealtimeWeatherService weatherService;

    private Double humidity = 50.00;

    private Double soilMoisture = 50.00;

    private Double temperature = 20.00;

    @Autowired
    private SensorReadRepository sensorReadRepository;

    @Autowired
    private IrrigationRepository irrigationRepository;

    @Override
    public void doWatering(long duration) {

        // communicationService.writeAction(new Action(IOTAction.WATER, new WateringData(duration)));
    }

    @Scheduled(fixedDelay = 15 * 60 * 1000)
    public void reloadMeasurement() {

    }

    @Scheduled(fixedDelay = 15 * 60 * 1000)
    public void wateringCheck() {

    }

    public Optional<Irrigation> findSimilar(Location location){
        List<Irrigation> irrigationList = irrigationRepository.findByMonthAndLocation(location);

        return Optional.ofNullable(irrigationList.stream()
                .sorted(Comparator.comparing(this::calculateScore))
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
