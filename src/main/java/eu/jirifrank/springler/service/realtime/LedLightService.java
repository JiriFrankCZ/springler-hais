package eu.jirifrank.springler.service.realtime;

import eu.jirifrank.springler.api.action.Action;
import eu.jirifrank.springler.api.action.LightData;
import eu.jirifrank.springler.api.enums.DeviceAction;
import eu.jirifrank.springler.api.enums.ServiceType;
import eu.jirifrank.springler.service.communication.CommunicationService;
import eu.jirifrank.springler.service.logging.LoggingService;
import eu.jirifrank.springler.util.TimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

@Service
@Slf4j
public class LedLightService implements LightService {

    @Value("${lightning.automatic}")
    private Boolean automaticLightning;

    @Value("${lightning.duration}")
    private Integer automaticLightningDuration;

    @Value("${lightning.color}")
    private Integer defaultColor;

    @Autowired
    private CommunicationService communicationService;

    @Autowired
    private TaskScheduler taskScheduler;

    @Autowired
    private RealtimeWeatherService realtimeWeatherService;

    @Autowired
    private LoggingService loggingService;

    @PostConstruct
    public void init() {
        log.info("Lightning service started.");
        stopLight();

        if (automaticLightning) {
            log.info("Automatic lightning enabled, scheduling start/stop sequences.");
            LocalDateTime sunset = realtimeWeatherService.getSunset().plusMinutes(30);

            if (sunset.plusHours(automaticLightningDuration).isAfter(LocalDateTime.now()) && sunset.isBefore(LocalDateTime.now())) {
                scheduleStartLightAndExecute();
            } else {
                Date startTime = TimeUtils.fromDateTimeToDate(sunset);
                taskScheduler.schedule(this::scheduleStartLightAndExecute, startTime);
            }

            taskScheduler.schedule(this::scheduleStopLightAndExecute, getNextStopLightDate());
        }
    }

    @Override
    public void startLight(int color) {
        log.info("Lightning command will be sent with color {}.", color);
        LightData data = new LightData(color, null);
        Action action = new Action(DeviceAction.START_LIGHT, data);
        communicationService.sendActionMessage(action);
        loggingService.log("Lightning command has been sent.", ServiceType.LIGHTNING);
    }

    @Override
    public void stopLight() {
        log.info("Stop lightning command will be sent.");
        Action action = new Action(DeviceAction.STOP_LIGHT, null);
        communicationService.sendActionMessage(action);
        loggingService.log("Stop lightning command has been sent.", ServiceType.LIGHTNING);
    }

    @Override
    public void scheduleLight(int color, long duration) {
        log.info("Scheduled lightning for concrete period[{}s].", duration);
        startLight(color);
        taskScheduler.schedule(this::stopLight, Instant.now().plusSeconds(duration));
        loggingService.log("Scheduled lightning for concrete period " + duration + "s.", ServiceType.LIGHTNING);
    }

    private void scheduleStartLightAndExecute() {
        startLight(defaultColor);
        Date startTime = TimeUtils.fromDateTimeToDate(realtimeWeatherService.getSunset().plusDays(1).plusMinutes(30));
        taskScheduler.schedule(this::scheduleStartLightAndExecute, startTime);
        log.info("Next start of lightning is scheduled on {}.", startTime);
        loggingService.log("Scheduled periodic start lightning according to sunset[" + startTime.toString() + "].", ServiceType.LIGHTNING);
    }

    private void scheduleStopLightAndExecute() {
        stopLight();
        Date stopDateTime = getNextStopLightDate();
        taskScheduler.schedule(this::scheduleStopLightAndExecute, stopDateTime);
        log.info("Next stop of lightning is scheduled on {}.", stopDateTime);
        loggingService.log("Scheduled periodic lightning stop according to sunset[" + stopDateTime.toString() + "].", ServiceType.LIGHTNING);
    }

    private Date getNextStopLightDate(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 0);
        calendar.setTimeZone(TimeZone.getTimeZone(TimeUtils.ZONE_ID));
        return calendar.getTime();
    }
}