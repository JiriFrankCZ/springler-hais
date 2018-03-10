package eu.jirifrank.springler.service.communication.transport;

import eu.jirifrank.springler.api.enums.Location;
import eu.jirifrank.springler.api.enums.SensorType;

import java.util.Date;

public class SensorMessage {
    public String name;
    public Location location;
    public Date created;
    public SensorType type;
    public Double value;
}
