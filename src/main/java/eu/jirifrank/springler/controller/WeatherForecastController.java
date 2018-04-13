package eu.jirifrank.springler.controller;

import eu.jirifrank.springler.api.response.SingleValueResponse;
import eu.jirifrank.springler.service.realtime.RealtimeWeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/weather-forecast")
public class WeatherForecastController {

    @Autowired
    private RealtimeWeatherService realtimeWeatherService;

    @GetMapping("/temperature")
    public SingleValueResponse<Double> getWeatherForecast() {
        return new SingleValueResponse(realtimeWeatherService.getForecastedTemperature());
    }
}
