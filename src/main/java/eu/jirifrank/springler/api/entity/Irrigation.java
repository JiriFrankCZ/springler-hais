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
public class Irrigation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column
    private long id;

    @OneToMany(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
    private List<SensorRead> sensorReads;

    @Column
    @Enumerated(EnumType.STRING)
    private Location location;

    @Column
    private Double duration;

    @Column
    private Double correction;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;
}
