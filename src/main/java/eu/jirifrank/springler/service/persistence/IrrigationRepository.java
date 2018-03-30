package eu.jirifrank.springler.service.persistence;

import eu.jirifrank.springler.api.entity.Irrigation;
import eu.jirifrank.springler.api.enums.Location;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IrrigationRepository extends PagingAndSortingRepository<Irrigation, Long> {

    @Query(value = "select i from Irrigation i " +
            "join fetch i.sensorReads s " +
            "where month(i.date) = month(current_date()) and " +
            "i.location = :location and " +
            "i.correction is not null")
    List<Irrigation> findByMonthAndLocation(@Param("location") Location location);

    Irrigation findFirstByLocationOrderByDateDesc(Location location);

}