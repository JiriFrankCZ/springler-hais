package eu.jirifrank.springler.controller;

import eu.jirifrank.springler.AbstractIntegrationTest;
import eu.jirifrank.springler.api.enums.Location;
import eu.jirifrank.springler.api.enums.SensorType;
import eu.jirifrank.springler.api.request.SensorReadRequest;
import org.junit.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class SensorControllerTest extends AbstractIntegrationTest {


    @Test
    public void shouldReturnDefaultMessage() throws Exception {
        SensorReadRequest request = SensorReadRequest.builder()
                .sensorType(SensorType.HUMIDITY)
                .location(Location.OPENED)
                .value(20.2)
                .build();

        this.mockMvc.perform(post("/sensor/read", request))
                .andDo(print())
                .andExpect(status().isOk());
    }
}