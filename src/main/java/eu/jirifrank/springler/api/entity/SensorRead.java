package eu.jirifrank.springler.api.entity;

import eu.jirifrank.springler.api.enums.SensorType;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
public class SensorRead {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column
    private long id;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    @Column
    @Enumerated(EnumType.STRING)
    private SensorType sensorType;

    @Column
    private String rarityLevel;

    @Column
    private int year;

}
