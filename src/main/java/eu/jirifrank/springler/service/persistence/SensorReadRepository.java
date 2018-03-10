package eu.jirifrank.springler.service.persistence;

import eu.jirifrank.springler.api.entity.SensorRead;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SensorReadRepository extends PagingAndSortingRepository<SensorRead, Long> {
}
