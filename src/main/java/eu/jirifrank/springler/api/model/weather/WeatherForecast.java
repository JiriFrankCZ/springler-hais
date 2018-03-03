package eu.jirifrank.springler.api.model.weather;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class WeatherForecast {
    private String summary;
    private LocalDateTime sunrise;
    private LocalDateTime sunset;
    private Double maxTemperature;
    private Double minTemperature;
    private Double rainProbability;
    private Double humidity;
    private Double windSpeed;
}
