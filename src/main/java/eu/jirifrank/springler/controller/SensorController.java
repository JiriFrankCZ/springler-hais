package eu.jirifrank.springler.controller;

import eu.jirifrank.springler.api.request.SensorReadRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("sensor")
public class SensorController {

    @PostMapping(path = "read")
    public void read(SensorReadRequest readRequest) {

    }
}
