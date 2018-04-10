package eu.jirifrank.springler.service.realtime;

import eu.jirifrank.springler.api.enums.Location;

public interface IrrigationService {
    /**
     * Performs watering
     */
    void doWatering(Double duration, Location location);
}
