package eu.jirifrank.springler.api.entity;

import eu.jirifrank.springler.api.enums.Location;
import eu.jirifrank.springler.api.enums.SensorType;
import eu.jirifrank.springler.api.enums.ServiceType;
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
        @Index(name = "idx_type_location_service", columnList = "sensorType,serviceType,location")
})
public class SensorRead {
    @Id
    @GeneratedValue(generator = "hibernate_sequence")
    @SequenceGenerator(name = "hibernate_sequence", sequenceName = "hibernate_sequence", allocationSize = 1)
    @Column
    private long id;

    @Column
    @Enumerated(EnumType.STRING)
    private ServiceType serviceType;

    @Column
    @Enumerated(EnumType.STRING)
    private SensorType sensorType;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @Column
    @Enumerated(EnumType.STRING)
    private Location location;

    @Column
    private Double value;

    @ManyToMany(cascade = {CascadeType.REFRESH, CascadeType.MERGE}, fetch = FetchType.LAZY, mappedBy = "sensorReads")
    private List<Irrigation> irrigationList;
}
