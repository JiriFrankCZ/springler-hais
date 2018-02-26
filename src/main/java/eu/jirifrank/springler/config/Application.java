package eu.jirifrank.springler.config;

import eu.jirifrank.springler.api.enums.ApplicationLocation;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = ApplicationLocation.ENTITIES)
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
