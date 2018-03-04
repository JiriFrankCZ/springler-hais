package eu.jirifrank.springler.api.action;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WateringData implements ActionObject {
    private long duration;
}
