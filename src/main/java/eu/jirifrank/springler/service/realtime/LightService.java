package eu.jirifrank.springler.service.realtime;

public interface LightService {
    void startLight(int color);

    void stopLight();

    void scheduleLight(int color, long duration);
}
