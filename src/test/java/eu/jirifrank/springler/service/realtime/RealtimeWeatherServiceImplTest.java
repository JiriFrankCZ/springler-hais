package eu.jirifrank.springler.service.realtime;

import eu.jirifrank.springler.api.model.weather.WeatherForecast;
import eu.jirifrank.springler.service.weather.WeatherService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;


@RunWith(SpringJUnit4ClassRunner.class)
public class RealtimeWeatherServiceImplTest {

    private static final Double REFERENCE_RAIN_THRESHOLD = 80.00;

    @Mock
    private WeatherService weatherService;


    @InjectMocks
    private RealtimeWeatherServiceImpl realtimeWeatherService;

    @Before
    public void before() {
        ReflectionTestUtils.setField(realtimeWeatherService, "rainProbabilityThreshold", REFERENCE_RAIN_THRESHOLD);
    }

    @Test
    public void isRainPredicted_notPredicted_resultNo() {
        givenRainForecast(10.00);

        assertFalse(realtimeWeatherService.isRainPredicted());
    }

    @Test
    public void isRainPredicted_notPredicted_resultYes() {
        givenRainForecast(81.00);

        assertTrue(realtimeWeatherService.isRainPredicted());
    }

    private void givenRainForecast(double rainProbability) {
        WeatherForecast weatherForecast = new WeatherForecast();
        weatherForecast.setRainProbability(rainProbability);
        doReturn(weatherForecast).when(weatherService).getForecast();
        realtimeWeatherService.init();
    }
}