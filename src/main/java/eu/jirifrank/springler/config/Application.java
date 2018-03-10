package eu.jirifrank.springler.config;

import eu.jirifrank.springler.api.enums.ApplicationLocation;
import eu.jirifrank.springler.service.notification.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.PostConstruct;

@EnableAsync
@EnableJpaRepositories(basePackages = ApplicationLocation.REPOSITORIES)
@EnableRetry
@EnableScheduling
@EntityScan(basePackages = ApplicationLocation.ENTITIES)
@SpringBootApplication(scanBasePackages = ApplicationLocation.BASE_PACKAGE)
public class Application {

    @Autowired
    private NotificationService notificationService;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @PostConstruct
    public void init() {
        notificationService.send("System status", "Application has been started and is running.");
    }
}
