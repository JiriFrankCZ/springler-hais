package eu.jirifrank.springler.api.entity;

import eu.jirifrank.springler.api.enums.ServiceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Log {
    @Id
    @GeneratedValue(generator = "hibernate_sequence")
    @SequenceGenerator(name = "hibernate_sequence", sequenceName = "hibernate_sequence", allocationSize = 1)
    @Column
    private long id;

    @Column
    @Enumerated(EnumType.STRING)
    private ServiceType serviceType;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @Column
    private String message;
}
