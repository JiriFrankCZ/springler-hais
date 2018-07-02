package eu.jirifrank.springler.service.persistence;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.io.Serializable;

@NoRepositoryBean
public interface ImprovedCrudRepository<T, ID extends Serializable>
        extends PagingAndSortingRepository<T, ID> {
    void refresh(T t);
}