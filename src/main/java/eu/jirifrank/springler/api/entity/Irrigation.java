package eu.jirifrank.springler.api.entity;

import eu.jirifrank.springler.api.enums.Location;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Irrigation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column
    private long id;

    @OneToMany(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
    private List<SensorRead> sensorReads;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<WateringLearning> wateringLearnings;

    @Column
    private Double correction;

    @Column
    @Enumerated(EnumType.STRING)
    private Location location;
}
