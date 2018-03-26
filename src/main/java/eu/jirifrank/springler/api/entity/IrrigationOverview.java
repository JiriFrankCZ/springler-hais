package eu.jirifrank.springler.api.entity;

import org.hibernate.annotations.Subselect;
import org.hibernate.annotations.Synchronize;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Subselect(
        "select * from irrigation"
)
@Synchronize({"sensor_read"})
public class IrrigationOverview {
    @Id
    @Column
    private long id;
}
