package eu.jirifrank.springler.service.persistence;

import eu.jirifrank.springler.api.entity.SensorRead;
import eu.jirifrank.springler.api.enums.SensorType;
import eu.jirifrank.springler.api.enums.ServiceType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SensorReadRepository extends PagingAndSortingRepository<SensorRead, Long> {

    @Query(value = "select s from SensorRead s " +
            "left join fetch s.irrigationList i " +
            "where s.id IN (" +
            "select max(sm.id) from SensorRead sm " +
            "group by sm.location, sm.sensorType, sm.serviceType " +
            "having sm.sensorType = :sensorType and " +
            "sm.serviceType = :serviceType" +
            ") ")
    List<SensorRead> findLatestByType(@Param("sensorType") SensorType sensorType, @Param("serviceType") ServiceType serviceType);
}
