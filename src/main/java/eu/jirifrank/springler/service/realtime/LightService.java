package eu.jirifrank.springler.service.realtime;

public interface LightService {
    void startLight(int r, int g, int b);

    void stopLight();

    void scheduleLight(int r, int g, int b, long duration);
}
