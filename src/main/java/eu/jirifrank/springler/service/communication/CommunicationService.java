package eu.jirifrank.springler.service.communication;

import eu.jirifrank.springler.api.action.Action;

public interface CommunicationService {
    void sendActionMessage(Action action);
}
