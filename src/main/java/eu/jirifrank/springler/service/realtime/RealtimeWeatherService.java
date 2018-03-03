package eu.jirifrank.springler.service.realtime;

public interface RealtimeWeatherService {
    /**
     * Checkes whether forecast predicts high probable rain.
     */
    boolean isRainPredicted();
}
