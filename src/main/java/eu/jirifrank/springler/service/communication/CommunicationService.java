package eu.jirifrank.springler.service.communication;

import eu.jirifrank.springler.api.action.ActionObject;

public interface CommunicationService {

    void sendActionMessage(ActionObject actionObject);
}
