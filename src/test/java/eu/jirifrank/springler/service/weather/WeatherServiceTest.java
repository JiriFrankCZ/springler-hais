package eu.jirifrank.springler.service.weather;

import eu.jirifrank.springler.AbstractIntegrationTest;
import eu.jirifrank.springler.service.realtime.RealtimeWeatherService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class WeatherServiceTest extends AbstractIntegrationTest {

    @Autowired
    private WeatherService weatherService;

    @Autowired
    private RealtimeWeatherService realtimeWeatherService;

    @Test
    public void standardWeatherForecastTest() {
        Assert.assertNotNull(weatherService.getForecast());
        Assert.assertNotNull(realtimeWeatherService.isRainPredicted());
    }
}