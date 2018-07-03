package eu.jirifrank.springler.service.persistence;

import eu.jirifrank.springler.api.entity.Irrigation;
import eu.jirifrank.springler.api.enums.Location;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IrrigationRepository extends ImprovedCrudRepository<Irrigation, Long> {

    @Query(value = "select distinct i from Irrigation i " +
            "join fetch i.sensorReads s " +
            "where (month(i.created) = month(current_date()) or month(i.updated) = month(current_date())) and " +
            "i.location = :location and " +
            "i.correction is not null")
    List<Irrigation> findByMonthAndLocation(@Param("location") Location location);

    Irrigation findFirstByLocationOrderByCreatedDesc(Location location);

}