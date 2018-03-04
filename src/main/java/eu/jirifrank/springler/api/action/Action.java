package eu.jirifrank.springler.api.action;

import eu.jirifrank.springler.api.enums.IOTAction;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Action {
    private IOTAction action;
    private ActionObject data;
}
