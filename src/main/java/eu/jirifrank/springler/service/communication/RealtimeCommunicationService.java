package eu.jirifrank.springler.service.communication;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.jirifrank.springler.api.action.Action;
import eu.jirifrank.springler.api.entity.SensorRead;
import eu.jirifrank.springler.api.enums.ApplicationLocation;
import eu.jirifrank.springler.api.request.LogRequest;
import eu.jirifrank.springler.api.request.SensorReadRequestList;
import eu.jirifrank.springler.service.logging.LoggingService;
import eu.jirifrank.springler.service.persistence.SensorReadRepository;
import eu.jirifrank.springler.util.NumberUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.io.IOException;

@Service
@Slf4j
public class RealtimeCommunicationService implements CommunicationService {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final Validator validator;

    static {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private SensorReadRepository sensorReadRepository;

    @Autowired
    private LoggingService loggingService;

    @Transactional
    @RabbitListener(queues = {ApplicationLocation.MQ_QUEUE_MEASUREMENTS})
    public void receiveSensorMessage(Message message) {
        log.debug(message.toString());

        SensorReadRequestList sensorReadRequestList = deserializeFromByteArray(message.getBody(), SensorReadRequestList.class);

        if (sensorReadRequestList == null || sensorReadRequestList.getData() == null) {
            log.warn("Not valid message on input stream.");
            return;
        }

        sensorReadRequestList.getData()
                .parallelStream()
                .forEach(sensorReadRequest -> {
                    if (sensorReadRequest == null || !validator.validate(sensorReadRequest).isEmpty()) {
                        log.warn("Message thrown away.", message);
                        return;
                    }

                    SensorRead sensorRead = SensorRead.builder()
                            .serviceType(sensorReadRequest.getServiceType())
                            .sensorType(sensorReadRequest.getSensorType())
                            .created(message.getMessageProperties().getTimestamp())
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
                });
    }

    @RabbitListener(queues = {ApplicationLocation.MQ_QUEUE_LOGS})
    public void recieveLogMessage(Message message) {
        log.debug(message.toString());

        LogRequest logRequest = deserializeFromByteArray(message.getBody(), LogRequest.class);

        if (logRequest == null || !validator.validate(logRequest).isEmpty()) {
            log.warn("Message thrown away.", message);
            return;
        }

        loggingService.log(
                logRequest.getMessage(),
                logRequest.getServiceType()
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
