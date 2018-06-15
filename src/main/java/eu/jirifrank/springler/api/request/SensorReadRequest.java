package eu.jirifrank.springler.api.request;

import eu.jirifrank.springler.api.enums.Location;
import eu.jirifrank.springler.api.enums.SensorType;
import eu.jirifrank.springler.api.enums.ServiceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SensorReadRequest {

    @NotNull
    private ServiceType serviceType;

    @NotNull
    private SensorType sensorType;

    @NotNull
    private Double value;

    @NotNull
    private Location location;

    private Date created = new Date();
}
