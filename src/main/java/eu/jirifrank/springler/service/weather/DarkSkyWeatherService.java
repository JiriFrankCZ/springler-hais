package eu.jirifrank.springler.service.weather;

import eu.jirifrank.springler.api.model.darksky.ForecastData;
import eu.jirifrank.springler.api.model.darksky.ForecastResponse;
import eu.jirifrank.springler.api.model.weather.WeatherForecast;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Service
@Slf4j
public class DarkSkyWeatherService implements WeatherService {

    private static final String BASE_PATH = "https://api.darksky.net/forecast";

    private static final String STATIC_PARAMS = "exclude=currently,hourly,minutely,alerts,flags&lang=cs&units=si";

    private static final String PRECIPE_TYPE_RAIN = "rain";

    @Value("${weather.api.key}")
    private String secretKey;

    @Value("${weather.location.latitude}")
    private String longitude;

    @Value("${weather.location.longitude}")
    private String latitude;

    private RestTemplate restTemplate = new RestTemplate();

    @Override
    public WeatherForecast getForecast() {
        log.info("Weather info request for {} and {} position.", latitude, longitude);

        StringBuilder sb = new StringBuilder();
        sb.append(BASE_PATH);
        sb.append("/");
        sb.append(secretKey);
        sb.append("/");
        sb.append(longitude);
        sb.append(",");
        sb.append(latitude);
        sb.append("?");
        sb.append(STATIC_PARAMS);

        final String url = sb.toString();

        log.debug("Url for request has been built {}.", url);

        ResponseEntity<ForecastResponse> responseEntity = restTemplate.getForEntity(URI.create(url), ForecastResponse.class);

        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            log.info("Weather info request was successful.");

            ForecastData data = responseEntity.getBody().getDaily().getData().get(0);

            WeatherForecast weatherForecast = new WeatherForecast();
            weatherForecast.setMaxTemperature(data.getTemperatureMax());
            weatherForecast.setMinTemperature(data.getTemperatureMin());
            weatherForecast.setSunrise(data.getSunriseTime());
            weatherForecast.setSunset(data.getSunsetTime());
            weatherForecast.setHumidity(data.getHumidity());
            weatherForecast.setWindSpeed(data.getWindSpeed());
            weatherForecast.setSummary(data.getSummary());
            weatherForecast.setRainProbability((data.getPrecipType() != null && data.getPrecipType().equalsIgnoreCase(PRECIPE_TYPE_RAIN)) ? data.getPrecipProbability() : 0);

            log.debug("Weather forecast resolved {}.", weatherForecast);

            return weatherForecast;
        } else {
            log.error("Error occured during call of Dark Sky API.", responseEntity.getStatusCode());
            return null;
        }
    }
}