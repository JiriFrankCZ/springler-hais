package eu.jirifrank.springler.api.action;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LightData implements ActionObject {
    private Integer rColor;
    private Integer gColor;
    private Integer bColor;
    private Long duration;
}
