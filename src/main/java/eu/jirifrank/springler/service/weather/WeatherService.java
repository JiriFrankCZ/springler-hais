package eu.jirifrank.springler.service.weather;

import eu.jirifrank.springler.api.model.weather.WeatherForecast;

public interface WeatherService {
    /**
     * Resolves actual weather forecast based on specified lat and long data via properties.
     */
    WeatherForecast getForecast();
}
