package eu.jirifrank.springler.controller;

import eu.jirifrank.springler.api.response.SingleValueResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/configuration")
public class ConfigurationController {

    @Value("${configuration.sleep}")
    private long sleepPeriod;

    @GetMapping("/sleep")
    public SingleValueResponse<Long> getSleepPeriod() {
        return new SingleValueResponse(sleepPeriod);
    }
}
