package eu.jirifrank.springler.api.action;

import eu.jirifrank.springler.api.enums.Location;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WateringData implements ActionObject {
    private Double duration;
    private Location location;
}
