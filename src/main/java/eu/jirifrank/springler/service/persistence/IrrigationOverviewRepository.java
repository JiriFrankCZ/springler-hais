package eu.jirifrank.springler.service.persistence;

import eu.jirifrank.springler.api.entity.Irrigation;
import eu.jirifrank.springler.api.entity.IrrigationOverview;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IrrigationOverviewRepository extends PagingAndSortingRepository<IrrigationOverview, Long> {

//    @Query(value = "select i from Irrigation i " +
//            "join fetch i.sensorReads " +
//            "where month(s.date) = month(current_date()) and " +
//            "s.location = :location and " +
//            "s.sensorType = :sensorType " +
//            "order by abs(s.value - :value)")
//    Irrigation findSimilar(@Param("location") Location location, @Param("sensorType") SensorType sensorType, @Param("value") Double value);

}