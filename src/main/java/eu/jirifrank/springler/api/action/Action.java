package eu.jirifrank.springler.api.action;

import eu.jirifrank.springler.api.enums.IOTAction;
import lombok.Data;

@Data
public class Action {
    private IOTAction action;
    private ActionObject data;
}
