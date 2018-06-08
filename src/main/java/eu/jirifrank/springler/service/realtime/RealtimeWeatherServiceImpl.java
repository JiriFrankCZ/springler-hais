package eu.jirifrank.springler.service.realtime;

import eu.jirifrank.springler.api.entity.SensorRead;
import eu.jirifrank.springler.api.enums.Location;
import eu.jirifrank.springler.api.enums.SensorType;
import eu.jirifrank.springler.api.enums.ServiceType;
import eu.jirifrank.springler.api.model.weather.WeatherForecast;
import eu.jirifrank.springler.service.persistence.SensorReadRepository;
import eu.jirifrank.springler.service.weather.WeatherService;
import eu.jirifrank.springler.util.NumberUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class RealtimeWeatherServiceImpl implements RealtimeWeatherService {

    @Value("${weather.rain.threshold}")
    private Double rainProbabilityThreshold;

    private WeatherForecast weatherForecast;

    @Autowired
    private WeatherService weatherService;

    @Autowired
    private SensorReadRepository sensorReadRepository;

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

        List<SensorRead> sensorReadList = new ArrayList<>();

        sensorReadList.add(
                SensorRead.builder()
                        .serviceType(ServiceType.WEATHER)
                        .sensorType(SensorType.RAIN)
                        .created(new Date())
                        .location(Location.ALL)
                        .value(NumberUtils.roundToHalf(weatherForecast.getRainProbability()))
                        .build()
        );

        sensorReadList.add(
                SensorRead.builder()
                        .serviceType(ServiceType.WEATHER)
                        .sensorType(SensorType.HUMIDITY)
                        .created(new Date())
                        .location(Location.ALL)
                        .value(NumberUtils.roundToHalf(weatherForecast.getHumidity() * 100))
                        .build()
        );

        sensorReadList.add(
                SensorRead.builder()
                        .serviceType(ServiceType.WEATHER)
                        .sensorType(SensorType.TEMPERATURE)
                        .created(new Date())
                        .location(Location.ALL)
                        .value(NumberUtils.roundToHalf(weatherForecast.getMaxTemperature()))
                        .build()
        );

        sensorReadRepository.saveAll(sensorReadList);

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
