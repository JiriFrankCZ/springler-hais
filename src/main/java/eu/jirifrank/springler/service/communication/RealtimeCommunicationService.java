package eu.jirifrank.springler.service.communication;

import eu.jirifrank.springler.api.action.Action;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Service
@Slf4j
public class RealtimeCommunicationService implements CommunicationService {

    private BlockingQueue<Action> actionsQueue = new LinkedBlockingQueue();

    public Optional<Action> readAction() {
        try {
            Action action = actionsQueue.take();
            if (action != null) {
                return Optional.of(action);
            }
        } catch (InterruptedException e) {
            log.error("Error occurred during polling queue for action.", e);
        }

        return Optional.empty();
    }

    @Override
    @Retryable(value = {InterruptedException.class}, maxAttempts = 10, backoff = @Backoff(delay = 5000))
    public void writeAction(Action action) throws InterruptedException {
        actionsQueue.put(action);
    }
}
