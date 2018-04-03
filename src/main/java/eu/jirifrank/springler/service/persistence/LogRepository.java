package eu.jirifrank.springler.service.persistence;

import eu.jirifrank.springler.api.entity.Log;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogRepository extends PagingAndSortingRepository<Log, Long> {

}
