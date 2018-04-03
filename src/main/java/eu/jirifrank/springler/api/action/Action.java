package eu.jirifrank.springler.api.action;

import eu.jirifrank.springler.api.enums.DeviceAction;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Action {
    private DeviceAction action;
    private ActionObject data;
}
