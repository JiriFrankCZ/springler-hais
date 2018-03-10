package eu.jirifrank.springler.service.communication;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.jirifrank.springler.api.action.Action;
import eu.jirifrank.springler.api.action.WateringData;
import eu.jirifrank.springler.api.entity.SensorRead;
import eu.jirifrank.springler.api.enums.ApplicationLocation;
import eu.jirifrank.springler.api.enums.IOTAction;
import eu.jirifrank.springler.service.notification.NotificationService;
import eu.jirifrank.springler.service.persistence.SensorReadRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
public class RealtimeCommunicationService implements CommunicationService {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private SensorReadRepository sensorReadRepository;

    @Autowired
    private NotificationService notificationService;

    public void requestAction(Action action) {
        byte[] serializedAction = serializeToByteArray(action);
        rabbitTemplate.convertAndSend(serializedAction);
    }

    @RabbitListener(queues = {ApplicationLocation.MQ_QUEUE_MEASUREMENTS})
    public void recieveSensorMessage(Message message) {
        log.debug(message.toString());

        SensorRead sensorRead = new SensorRead();
//        sensorRead.setDate();
//        sensorReadRepository.save(SensorRead)
        requestAction(new Action(IOTAction.SCAN_RAIN, new WateringData(10)));
    }

    private byte[] serializeToByteArray(Action action) {
        try {
            return OBJECT_MAPPER.writeValueAsBytes(action);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Not valid object passed for serialization to byte array.", e);
        }
    }

    private <T> T deserializeFromByteArray(byte[] source, Class<T> toClass) {
        try {
            return OBJECT_MAPPER.readValue(source, toClass);
        } catch (IOException e) {
            log.error("Deserialization error.", e);
        }

        return null;
    }
}
