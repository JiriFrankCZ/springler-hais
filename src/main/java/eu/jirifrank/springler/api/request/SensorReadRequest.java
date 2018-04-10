package eu.jirifrank.springler.api.request;

import eu.jirifrank.springler.api.enums.Location;
import eu.jirifrank.springler.api.enums.SensorType;
import eu.jirifrank.springler.api.enums.ServiceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SensorReadRequest {
    private ServiceType serviceType;
    private SensorType sensorType;
    private Double value;
    private Location location;
}
