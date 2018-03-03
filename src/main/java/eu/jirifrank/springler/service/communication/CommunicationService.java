package eu.jirifrank.springler.service.communication;

import eu.jirifrank.springler.api.action.Action;

import java.util.Optional;

public interface CommunicationService {
    /**
     * Performs read pending action to be processed
     */
    Optional<Action> readAction();

    /**
     * Writes action for processing in queue
     */
    void writeAction(Action action) throws InterruptedException;
}
