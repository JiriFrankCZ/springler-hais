package eu.jirifrank.springler.service.realtime;

import eu.jirifrank.springler.AbstractIntegrationTest;
import eu.jirifrank.springler.api.entity.Irrigation;
import eu.jirifrank.springler.api.entity.IrrigationOverview;
import eu.jirifrank.springler.api.entity.SensorRead;
import eu.jirifrank.springler.api.enums.Location;
import eu.jirifrank.springler.api.enums.SensorType;
import eu.jirifrank.springler.service.persistence.IrrigationOverviewRepository;
import eu.jirifrank.springler.service.persistence.IrrigationRepository;
import eu.jirifrank.springler.service.persistence.SensorReadRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Date;

import static org.junit.Assert.*;

/**
 * Popis tridy
 *
 * @author Jmeno Prijmeni (JPR)
 * @version 1.0
 * @since 23.3.2018
 */
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
                .sensorType(SensorType.HUMIDITY)
                .date(new Date())
                .location(Location.COVERED)
                .value(10.0)
                .build();

        sensorReadRepository.save(sensorRead);

        Irrigation irrigation = Irrigation
                .builder()
                .duration(10.0)
                .location(Location.COVERED)
                .date(new Date())
                .sensorReads(Arrays.asList(sensorRead))
                .build();

        irrigationRepository.save(irrigation);

        sensorRead = SensorRead.builder()
                .sensorType(SensorType.HUMIDITY)
                .date(new Date())
                .location(Location.COVERED)
                .value(20.0)
                .build();

        sensorReadRepository.save(sensorRead);

        irrigation = Irrigation
                .builder()
                .duration(15.0)
                .location(Location.COVERED)
                .date(new Date())
                .sensorReads(Arrays.asList(sensorRead))
                .build();

        irrigationRepository.save(irrigation);

        irrigationService.findSimilar(Location.COVERED);
    }
}