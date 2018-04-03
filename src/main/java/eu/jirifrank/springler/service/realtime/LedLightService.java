package eu.jirifrank.springler.service.realtime;

import eu.jirifrank.springler.api.action.Action;
import eu.jirifrank.springler.api.action.LightData;
import eu.jirifrank.springler.api.enums.DeviceAction;
import eu.jirifrank.springler.service.communication.CommunicationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@Slf4j
public class LedLightService implements LightService {

    @Autowired
    private CommunicationService communicationService;

    @Autowired
    private TaskScheduler taskScheduler;

    @Override
    public void startLight(int r, int g, int b) {
        log.info("Lightning command will be sent with combination of RGB({},{},{}).", r, b, b);
        LightData data = new LightData(r, g, b, null);
        Action action = new Action(DeviceAction.START_LIGHT, data);
        communicationService.sendActionMessage(action);
    }

    @Override
    public void stopLight() {
        log.info("Stop lightning command will be sent.");
        Action action = new Action(DeviceAction.STOP_LIGHT, null);
        communicationService.sendActionMessage(action);
    }

    @Override
    public void scheduleLight(int r, int g, int b, long duration) {
        startLight(r, g, b);
        taskScheduler.schedule(this::stopLight, Instant.now().plusSeconds(duration));
    }
}