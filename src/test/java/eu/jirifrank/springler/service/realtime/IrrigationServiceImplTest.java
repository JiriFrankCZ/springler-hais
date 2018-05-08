package eu.jirifrank.springler.service.realtime;

import eu.jirifrank.springler.AbstractIntegrationTest;
import eu.jirifrank.springler.api.entity.Irrigation;
import eu.jirifrank.springler.api.entity.SensorRead;
import eu.jirifrank.springler.api.enums.Location;
import eu.jirifrank.springler.api.enums.SensorType;
import eu.jirifrank.springler.api.enums.ServiceType;
import eu.jirifrank.springler.service.persistence.IrrigationRepository;
import eu.jirifrank.springler.service.persistence.SensorReadRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Date;

public class IrrigationServiceImplTest extends AbstractIntegrationTest {

    @Autowired
    private IrrigationServiceImpl irrigationService;

    @Autowired
    private IrrigationRepository irrigationRepository;

    @Autowired
    private SensorReadRepository sensorReadRepository;

    @Test
    public void doWatering() {
        SensorRead sensorRead = SensorRead.builder()
                .sensorType(SensorType.SOIL_MOISTURE)
                .created(new Date())
                .location(Location.COVERED)
                .serviceType(ServiceType.IRRIGATION)
                .value(10.0)
                .build();

        sensorReadRepository.save(sensorRead);

        Irrigation irrigation = Irrigation
                .builder()
                .duration(10.0)
                .location(Location.COVERED)
                .created(new Date())
                .rainProbability(40.0)
                .temperatureForecast(20.0)
                .sensorReads(Arrays.asList(sensorRead))
                .iteration(1)
                .build();

        irrigationRepository.save(irrigation);

        sensorRead = SensorRead.builder()
                .sensorType(SensorType.HUMIDITY)
                .created(new Date())
                .location(Location.COVERED)
                .serviceType(ServiceType.IRRIGATION)
                .value(20.0)
                .build();

        sensorReadRepository.save(sensorRead);

        irrigation = Irrigation
                .builder()
                .duration(15.0)
                .location(Location.COVERED)
                .correction(-1.0)
                .created(new Date())
                .rainProbability(40.0)
                .temperatureForecast(20.0)
                .iteration(1)
                .sensorReads(Arrays.asList(sensorRead))
                .build();

        irrigationRepository.save(irrigation);


        sensorRead = SensorRead.builder()
                .sensorType(SensorType.SOIL_MOISTURE)
                .created(new Date())
                .location(Location.COVERED)
                .serviceType(ServiceType.IRRIGATION)
                .value(10.0)
                .build();

        sensorReadRepository.save(sensorRead);

        sensorRead = SensorRead.builder()
                .sensorType(SensorType.SOIL_MOISTURE)
                .created(new Date())
                .location(Location.COVERED)
                .serviceType(ServiceType.IRRIGATION)
                .value(10.0)
                .build();

        sensorReadRepository.save(sensorRead);

        sensorRead = SensorRead.builder()
                .sensorType(SensorType.TEMPERATURE)
                .created(new Date())
                .location(Location.COVERED)
                .serviceType(ServiceType.IRRIGATION)
                .value(10.0)
                .build();

        sensorReadRepository.save(sensorRead);


        sensorRead = SensorRead.builder()
                .sensorType(SensorType.HUMIDITY)
                .created(new Date())
                .location(Location.OPENED)
                .serviceType(ServiceType.IRRIGATION)
                .value(1.0)
                .build();

        sensorReadRepository.save(sensorRead);

        sensorRead = SensorRead.builder()
                .sensorType(SensorType.SOIL_MOISTURE)
                .created(new Date())
                .location(Location.OPENED)
                .serviceType(ServiceType.IRRIGATION)
                .value(1.0)
                .build();

        sensorReadRepository.save(sensorRead);

        sensorRead = SensorRead.builder()
                .sensorType(SensorType.TEMPERATURE)
                .created(new Date())
                .location(Location.OPENED)
                .serviceType(ServiceType.IRRIGATION)
                .value(1.0)
                .build();

        sensorReadRepository.save(sensorRead);

        irrigationService.reloadMeasurement();
        irrigationService.wateringCheck();
    }
}