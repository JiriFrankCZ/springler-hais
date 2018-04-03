package eu.jirifrank.springler.service.persistence;

import eu.jirifrank.springler.api.entity.SensorRead;
import eu.jirifrank.springler.api.enums.SensorType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SensorReadRepository extends PagingAndSortingRepository<SensorRead, Long> {

    @Query(value = "select s from SensorRead s " +
            "where s.id IN (" +
            "select max(sm.id) from SensorRead sm " +
            "group by sm.location, sm.sensorType " +
            "having sm.sensorType = :sensorType" +
            ") ")
    List<SensorRead> findLatestByType(@Param("sensorType") SensorType sensorType);
}