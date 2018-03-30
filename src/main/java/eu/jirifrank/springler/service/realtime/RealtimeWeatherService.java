package eu.jirifrank.springler.service.realtime;

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
}
