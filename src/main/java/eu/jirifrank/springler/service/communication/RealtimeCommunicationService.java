package eu.jirifrank.springler.service.communication;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.jirifrank.springler.api.action.Action;
import eu.jirifrank.springler.api.entity.SensorRead;
import eu.jirifrank.springler.api.enums.ApplicationLocation;
import eu.jirifrank.springler.api.request.SensorReadRequest;
import eu.jirifrank.springler.service.logging.LoggingService;
import eu.jirifrank.springler.service.persistence.LogRepository;
import eu.jirifrank.springler.service.persistence.SensorReadRepository;
import eu.jirifrank.springler.util.NumberUtils;
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

    @Autowired(required = false)
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private SensorReadRepository sensorReadRepository;

    @Autowired
    private LoggingService loggingService;

    @RabbitListener(queues = {ApplicationLocation.MQ_QUEUE_MEASUREMENTS})
    public void recieveSensorMessage(Message message) {
        log.debug(message.toString());

        SensorReadRequest sensorReadRequest = deserializeFromByteArray(message.getBody(), SensorReadRequest.class);

        SensorRead sensorRead = SensorRead.builder()
                .serviceType(sensorReadRequest.getServiceType())
                .sensorType(sensorReadRequest.getSensorType())
                .created(new Date())
                .location(sensorReadRequest.getLocation())
                .value(NumberUtils.roundToHalf(sensorReadRequest.getValue()))
                .build();

        sensorReadRepository.save(sensorRead);
        loggingService.log("Sensor read["
                + sensorReadRequest.getSensorType() + " ,"
                + sensorReadRequest.getLocation() + ", "
                + sensorReadRequest.getValue() + "] was saved.",
                sensorReadRequest.getServiceType()
        );
    }

    @Override
    public void sendActionMessage(Action action) {
        byte[] serializedAction = serializeToByteArray(action);
        rabbitTemplate.convertAndSend(ApplicationLocation.MQ_QUEUE_DEFAULT_EXCHANGE, ApplicationLocation.MQ_QUEUE_ACTIONS, serializedAction);
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
