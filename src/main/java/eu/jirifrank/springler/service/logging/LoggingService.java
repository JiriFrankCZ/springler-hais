package eu.jirifrank.springler.service.logging;

import eu.jirifrank.springler.api.enums.ServiceType;

public interface LoggingService {
    void log(String message, ServiceType serviceType);
}
