package eu.jirifrank.springler.service.communication;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.jirifrank.springler.api.action.ActionObject;
import eu.jirifrank.springler.api.entity.SensorRead;
import eu.jirifrank.springler.api.enums.ApplicationLocation;
import eu.jirifrank.springler.api.request.SensorReadRequest;
import eu.jirifrank.springler.service.persistence.SensorReadRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;

@Service
@Slf4j
public class RealtimeCommunicationService implements CommunicationService {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private SensorReadRepository sensorReadRepository;

    @RabbitListener(queues = {ApplicationLocation.MQ_QUEUE_MEASUREMENTS})
    public void recieveSensorMessage(Message message) {
        log.debug(message.toString());

        SensorReadRequest sensorReadRequest = deserializeFromByteArray(message.getBody(), SensorReadRequest.class);

        SensorRead sensorRead = SensorRead.builder()
                .sensorType(sensorReadRequest.getSensorType())
                .date(new Date())
                .location(sensorReadRequest.getLocation())
                .value(sensorReadRequest.getValue())
                .build();

        sensorReadRepository.save(sensorRead);
    }

    @Override
    public void sendActionMessage(ActionObject actionObject) {
        byte[] serializedAction = serializeToByteArray(actionObject);
        rabbitTemplate.convertAndSend(ApplicationLocation.MQ_QUEUE_ACTIONS, "", serializedAction);
    }

    private byte[] serializeToByteArray(ActionObject actionObject) {
        try {
            return OBJECT_MAPPER.writeValueAsBytes(actionObject);
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
