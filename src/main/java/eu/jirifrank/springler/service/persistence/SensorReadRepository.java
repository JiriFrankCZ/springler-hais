package eu.jirifrank.springler.service.persistence;

import eu.jirifrank.springler.api.entity.SensorRead;
import eu.jirifrank.springler.api.enums.Location;
import eu.jirifrank.springler.api.enums.SensorType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SensorReadRepository extends PagingAndSortingRepository<SensorRead, Long> {

//    @Query(value = "select s from SensorRead s " +
//            "join fetch s.irrigation " +
//            "where month(s.date) = month(current_date()) and " +
//            "s.location = :location and " +
//            "s.sensorType = :sensorType " +
//            "order by abs(s.value - :value)")
//    SensorRead findSimilar(@Param("location") Location location, @Param("sensorType") SensorType sensorType, @Param("value") Double value);
}