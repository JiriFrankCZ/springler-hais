package eu.jirifrank.springler.service.persistence;

import eu.jirifrank.springler.api.entity.Log;
import org.springframework.stereotype.Repository;

@Repository
public interface LogRepository extends ImprovedCrudRepository<Log, Long> {

}
