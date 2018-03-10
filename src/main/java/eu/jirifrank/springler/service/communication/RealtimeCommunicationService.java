package eu.jirifrank.springler.service.communication;

import eu.jirifrank.springler.api.action.Action;
import eu.jirifrank.springler.api.exceptions.ConcurrentQueueProcessingException;
import eu.jirifrank.springler.service.notification.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Service
@Slf4j
public class RealtimeCommunicationService implements CommunicationService {

    @Autowired
    private NotificationService notificationService;

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
    @Retryable(value = {ConcurrentQueueProcessingException.class}, maxAttempts = 10, backoff = @Backoff(delay = 5000))
    public void writeAction(Action action) {
        try {
            actionsQueue.put(action);
        } catch (InterruptedException e) {
            log.error("Error occurred during puting action to the queue.", e);
            throw new ConcurrentQueueProcessingException();
        }
    }

    @Scheduled(fixedDelay = 5 * 60 * 1000)
    public void checkQueueProcessing() {
        if (actionsQueue.size() > 10 && actionsQueue.size() < 20) {
            log.warn("Potencial problems with queue processing. Number of unprocessed elements is too high.");
        } else if (actionsQueue.size() > 20) {
            log.error("Queue processing error. Actions are not beeing processed. Sending notification.");
            notificationService.send("Queue processing error",
                    "Number of elements in the queue is going to be too high. Probably there are problems in the IOT device." +
                            " Current number of elements: " + actionsQueue.size() + "."
            );
        }
    }

    @RabbitListener(queues = {"messarurementsExchangeName"})
    public void receiveMessageFromFanout1(String message) {
    }
}
