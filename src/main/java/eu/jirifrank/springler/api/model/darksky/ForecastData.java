package eu.jirifrank.springler.api.model.darksky;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import eu.jirifrank.springler.util.UnixDateTimeDeserializer;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "time",
        "summary",
        "icon",
        "sunriseTime",
        "sunsetTime",
        "moonPhase",
        "precipIntensity",
        "precipIntensityMax",
        "precipIntensityMaxTime",
        "precipProbability",
        "precipType",
        "temperatureMin",
        "temperatureMinTime",
        "temperatureMax",
        "temperatureMaxTime",
        "apparentTemperatureMin",
        "apparentTemperatureMinTime",
        "apparentTemperatureMax",
        "apparentTemperatureMaxTime",
        "dewPoint",
        "humidity",
        "windSpeed",
        "windBearing",
        "visibility",
        "cloudCover",
        "pressure",
        "ozone"
})
public class ForecastData {

    @JsonProperty("time")
    @JsonDeserialize(using = UnixDateTimeDeserializer.class)
    private LocalDateTime time;

    @JsonProperty("summary")
    private String summary;

    @JsonProperty("icon")
    private String icon;

    @JsonProperty("sunriseTime")
    @JsonDeserialize(using = UnixDateTimeDeserializer.class)
    private LocalDateTime sunriseTime;

    @JsonProperty("sunsetTime")
    @JsonDeserialize(using = UnixDateTimeDeserializer.class)
    private LocalDateTime sunsetTime;

    @JsonProperty("moonPhase")
    private Double moonPhase;

    @JsonProperty("precipIntensity")
    private Double precipIntensity;

    @JsonProperty("precipIntensityMax")
    private Double precipIntensityMax;

    @JsonProperty("precipIntensityMaxTime")
    private Integer precipIntensityMaxTime;

    @JsonProperty("precipProbability")
    private Double precipProbability;

    @JsonProperty("precipType")
    private String precipType;

    @JsonProperty("temperatureMin")
    private Double temperatureMin;

    @JsonProperty("temperatureMinTime")
    @JsonDeserialize(using = UnixDateTimeDeserializer.class)
    private LocalDateTime temperatureMinTime;

    @JsonProperty("temperatureMax")
    private Double temperatureMax;

    @JsonProperty("temperatureMaxTime")
    @JsonDeserialize(using = UnixDateTimeDeserializer.class)
    private LocalDateTime temperatureMaxTime;

    @JsonProperty("apparentTemperatureMin")
    private Double apparentTemperatureMin;

    @JsonProperty("apparentTemperatureMinTime")
    @JsonDeserialize(using = UnixDateTimeDeserializer.class)
    private LocalDateTime apparentTemperatureMinTime;

    @JsonProperty("apparentTemperatureMax")
    private Double apparentTemperatureMax;

    @JsonProperty("apparentTemperatureMaxTime")
    @JsonDeserialize(using = UnixDateTimeDeserializer.class)
    private LocalDateTime apparentTemperatureMaxTime;

    @JsonProperty("dewPoint")
    private Double dewPoint;

    @JsonProperty("humidity")
    private Double humidity;

    @JsonProperty("windSpeed")
    private Double windSpeed;

    @JsonProperty("windBearing")
    private Integer windBearing;

    @JsonProperty("visibility")
    private Double visibility;

    @JsonProperty("cloudCover")
    private Double cloudCover;

    @JsonProperty("pressure")
    private Double pressure;

    @JsonProperty("ozone")
    private Double ozone;

    @JsonProperty("time")
    @JsonDeserialize(using = UnixDateTimeDeserializer.class)
    public LocalDateTime getTime() {
        return time;
    }

    @JsonProperty("time")
    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    @JsonProperty("summary")
    public String getSummary() {
        return summary;
    }

    @JsonProperty("summary")
    public void setSummary(String summary) {
        this.summary = summary;
    }

    @JsonProperty("icon")
    public String getIcon() {
        return icon;
    }

    @JsonProperty("icon")
    public void setIcon(String icon) {
        this.icon = icon;
    }

    @JsonProperty("sunriseTime")
    public LocalDateTime getSunriseTime() {
        return sunriseTime;
    }

    @JsonProperty("sunriseTime")
    public void setSunriseTime(LocalDateTime sunriseTime) {
        this.sunriseTime = sunriseTime;
    }

    @JsonProperty("sunsetTime")
    public LocalDateTime getSunsetTime() {
        return sunsetTime;
    }

    @JsonProperty("sunsetTime")
    public void setSunsetTime(LocalDateTime sunsetTime) {
        this.sunsetTime = sunsetTime;
    }

    @JsonProperty("moonPhase")
    public Double getMoonPhase() {
        return moonPhase;
    }

    @JsonProperty("moonPhase")
    public void setMoonPhase(Double moonPhase) {
        this.moonPhase = moonPhase;
    }

    @JsonProperty("precipIntensity")
    public Double getPrecipIntensity() {
        return precipIntensity;
    }

    @JsonProperty("precipIntensity")
    public void setPrecipIntensity(Double precipIntensity) {
        this.precipIntensity = precipIntensity;
    }

    @JsonProperty("precipIntensityMax")
    public Double getPrecipIntensityMax() {
        return precipIntensityMax;
    }

    @JsonProperty("precipIntensityMax")
    public void setPrecipIntensityMax(Double precipIntensityMax) {
        this.precipIntensityMax = precipIntensityMax;
    }

    @JsonProperty("precipIntensityMaxTime")
    public Integer getPrecipIntensityMaxTime() {
        return precipIntensityMaxTime;
    }

    @JsonProperty("precipIntensityMaxTime")
    public void setPrecipIntensityMaxTime(Integer precipIntensityMaxTime) {
        this.precipIntensityMaxTime = precipIntensityMaxTime;
    }

    @JsonProperty("precipProbability")
    public Double getPrecipProbability() {
        return precipProbability;
    }

    @JsonProperty("precipProbability")
    public void setPrecipProbability(Double precipProbability) {
        this.precipProbability = precipProbability;
    }

    @JsonProperty("precipType")
    public String getPrecipType() {
        return precipType;
    }

    @JsonProperty("precipType")
    public void setPrecipType(String precipType) {
        this.precipType = precipType;
    }

    @JsonProperty("temperatureMin")
    public Double getTemperatureMin() {
        return temperatureMin;
    }

    @JsonProperty("temperatureMin")
    public void setTemperatureMin(Double temperatureMin) {
        this.temperatureMin = temperatureMin;
    }

    @JsonProperty("temperatureMinTime")
    public LocalDateTime getTemperatureMinTime() {
        return temperatureMinTime;
    }

    @JsonProperty("temperatureMinTime")
    public void setTemperatureMinTime(LocalDateTime temperatureMinTime) {
        this.temperatureMinTime = temperatureMinTime;
    }

    @JsonProperty("temperatureMax")
    public Double getTemperatureMax() {
        return temperatureMax;
    }

    @JsonProperty("temperatureMax")
    public void setTemperatureMax(Double temperatureMax) {
        this.temperatureMax = temperatureMax;
    }

    @JsonProperty("temperatureMaxTime")
    public LocalDateTime getTemperatureMaxTime() {
        return temperatureMaxTime;
    }

    @JsonProperty("temperatureMaxTime")
    public void setTemperatureMaxTime(LocalDateTime temperatureMaxTime) {
        this.temperatureMaxTime = temperatureMaxTime;
    }

    @JsonProperty("apparentTemperatureMin")
    public Double getApparentTemperatureMin() {
        return apparentTemperatureMin;
    }

    @JsonProperty("apparentTemperatureMin")
    public void setApparentTemperatureMin(Double apparentTemperatureMin) {
        this.apparentTemperatureMin = apparentTemperatureMin;
    }

    @JsonProperty("apparentTemperatureMinTime")
    public LocalDateTime getApparentTemperatureMinTime() {
        return apparentTemperatureMinTime;
    }

    @JsonProperty("apparentTemperatureMinTime")
    public void setApparentTemperatureMinTime(LocalDateTime apparentTemperatureMinTime) {
        this.apparentTemperatureMinTime = apparentTemperatureMinTime;
    }

    @JsonProperty("apparentTemperatureMax")
    public Double getApparentTemperatureMax() {
        return apparentTemperatureMax;
    }

    @JsonProperty("apparentTemperatureMax")
    public void setApparentTemperatureMax(Double apparentTemperatureMax) {
        this.apparentTemperatureMax = apparentTemperatureMax;
    }

    @JsonProperty("apparentTemperatureMaxTime")
    public LocalDateTime getApparentTemperatureMaxTime() {
        return apparentTemperatureMaxTime;
    }

    @JsonProperty("apparentTemperatureMaxTime")
    public void setApparentTemperatureMaxTime(LocalDateTime apparentTemperatureMaxTime) {
        this.apparentTemperatureMaxTime = apparentTemperatureMaxTime;
    }

    @JsonProperty("dewPoint")
    public Double getDewPoint() {
        return dewPoint;
    }

    @JsonProperty("dewPoint")
    public void setDewPoint(Double dewPoint) {
        this.dewPoint = dewPoint;
    }

    @JsonProperty("humidity")
    public Double getHumidity() {
        return humidity;
    }

    @JsonProperty("humidity")
    public void setHumidity(Double humidity) {
        this.humidity = humidity;
    }

    @JsonProperty("windSpeed")
    public Double getWindSpeed() {
        return windSpeed;
    }

    @JsonProperty("windSpeed")
    public void setWindSpeed(Double windSpeed) {
        this.windSpeed = windSpeed;
    }

    @JsonProperty("windBearing")
    public Integer getWindBearing() {
        return windBearing;
    }

    @JsonProperty("windBearing")
    public void setWindBearing(Integer windBearing) {
        this.windBearing = windBearing;
    }

    @JsonProperty("visibility")
    public Double getVisibility() {
        return visibility;
    }

    @JsonProperty("visibility")
    public void setVisibility(Double visibility) {
        this.visibility = visibility;
    }

    @JsonProperty("cloudCover")
    public Double getCloudCover() {
        return cloudCover;
    }

    @JsonProperty("cloudCover")
    public void setCloudCover(Double cloudCover) {
        this.cloudCover = cloudCover;
    }

    @JsonProperty("pressure")
    public Double getPressure() {
        return pressure;
    }

    @JsonProperty("pressure")
    public void setPressure(Double pressure) {
        this.pressure = pressure;
    }

    @JsonProperty("ozone")
    public Double getOzone() {
        return ozone;
    }

    @JsonProperty("ozone")
    public void setOzone(Double ozone) {
        this.ozone = ozone;
    }
}