package eu.jirifrank.springler.service.realtime;

import eu.jirifrank.springler.api.model.weather.WeatherForecast;
import eu.jirifrank.springler.service.notification.NotificationService;
import eu.jirifrank.springler.service.weather.WeatherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;

@Service
@Slf4j
public class RealtimeWeatherServiceImpl implements RealtimeWeatherService {

    @Autowired
    private NotificationService notificationService;

    @Value("${weather.rain.threshold}")
    private Double rainProbabilityThreshold;

    private WeatherForecast weatherForecast;

    @Autowired
    private WeatherService weatherService;

    @PostConstruct
    public void init() {
        log.info("Starting updating weather forecast.");
        weatherForecast = weatherService.getForecast();
        log.info("Weather forecast is up to created.");
    }

    @Scheduled(fixedDelay = 30 * 60 * 1000)
    public void periodicUpdate() {
        log.info("Periodic weather forecast update started.");
        weatherForecast = weatherService.getForecast();
        log.info("Periodic weather forecast update finished with actual data resolved.");
    }

    public boolean isRainPredicted() {
        double rainProbability = weatherForecast.getRainProbability();
        return rainProbability > rainProbabilityThreshold;
    }

    @Override
    public Double getRainProbability() {
        return weatherForecast.getRainProbability();
    }

    @Override
    public Double getForecastedTemperature() {
        return weatherForecast.getMaxTemperature();
    }

    @Override
    public LocalDateTime getSunset() {
        return weatherForecast.getSunset();
    }
}
