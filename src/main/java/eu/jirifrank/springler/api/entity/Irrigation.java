package eu.jirifrank.springler.api.entity;

import eu.jirifrank.springler.api.enums.Location;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(indexes = {
        @Index(name = "idx_location", columnList = "location")
})
public class Irrigation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column
    private long id;

    @ManyToMany(cascade = {CascadeType.REFRESH, CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    private List<SensorRead> sensorReads;

    @Column
    private Double rainProbability;

    @Column
    private Double temperatureForecast;

    @Column
    @Enumerated(EnumType.STRING)
    private Location location;

    @Column
    private Double duration;

    @Column
    private Integer iteration;

    @Column
    private Double correction;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date updated;
}
