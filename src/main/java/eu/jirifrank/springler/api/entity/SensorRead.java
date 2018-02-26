package eu.jirifrank.springler.api.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class SensorRead {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column
    private long id;

    @Column
    private String name;

    @Column
    private String rarityLevel;

    @Column
    private int year;

}
