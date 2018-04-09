package eu.jirifrank.springler.service.realtime;

import eu.jirifrank.springler.api.action.Action;
import eu.jirifrank.springler.api.action.LightData;
import eu.jirifrank.springler.api.enums.DeviceAction;
import eu.jirifrank.springler.service.communication.CommunicationService;
import eu.jirifrank.springler.util.TimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;

@Service
@Slf4j
public class LedLightService implements LightService {

    @Value("${lightning.automatic}")
    private Boolean automaticLightning;

    @Value("${lightning.duration}")
    private Integer automaticLightningDuration;

    @Value("${lightning.color.r}")
    private Integer rColor;

    @Value("${lightning.color.g}")
    private Integer gColor;

    @Value("${lightning.color.b}")
    private Integer bColor;

    @Autowired
    private CommunicationService communicationService;

    @Autowired
    private TaskScheduler taskScheduler;

    @Autowired
    private RealtimeWeatherService realtimeWeatherService;

    @PostConstruct
    public void init() {
        log.info("Lightning service started.");
        stopLight();

        if (automaticLightning) {
            log.info("Automatic lightning enabled, scheduling start/stop sequences.");
            LocalDateTime sunset = realtimeWeatherService.getSunset();

            Date startTime = TimeUtils.fromDateTimeToDate(sunset);
            taskScheduler.schedule(this::scheduleStartLightAndExecute, startTime);

            Date stopTime = TimeUtils.fromDateTimeToDate(sunset.plusHours(automaticLightningDuration));
            taskScheduler.schedule(this::scheduleStopLightAndExecute, stopTime);
        }
    }

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

    private void scheduleStartLightAndExecute() {
        startLight(rColor, gColor, bColor);
        Date startTime = TimeUtils.fromDateTimeToDate(realtimeWeatherService.getSunset().plusDays(1));
        taskScheduler.schedule(this::scheduleStartLightAndExecute, startTime);
        log.info("Next start of lightning is scheduled on {}.", startTime);
    }

    private void scheduleStopLightAndExecute() {
        stopLight();
        Date stopTime = TimeUtils.fromDateTimeToDate(
                realtimeWeatherService.getSunset().plusHours(automaticLightningDuration).plusDays(1)
        );
        taskScheduler.schedule(this::scheduleStopLightAndExecute, stopTime);
        log.info("Next stop of lightning is scheduled on {}.", stopTime);
    }
}