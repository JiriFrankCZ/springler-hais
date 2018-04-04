package eu.jirifrank.springler.service.realtime;

import java.time.LocalDateTime;

public interface RealtimeWeatherService {
    /**
     * Checkes whether forecast predicts high probable rain.
     */
    boolean isRainPredicted();

    /**
     * Return probability of rain for given day
     */
    Double getRainProbability();

    /**
     * Returns forecasted temperature in celcius for given day
     */
    Double getForecastedTemperature();

    /**
     * Returns forecasted sunset
     */
    LocalDateTime getSunset();
}
