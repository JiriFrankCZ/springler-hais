package eu.jirifrank.springler.api.action;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LightData implements ActionObject {
    private Integer color;
    private Long duration;
}
