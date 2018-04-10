package eu.jirifrank.springler.config;

import eu.jirifrank.springler.api.enums.ApplicationLocation;
import eu.jirifrank.springler.service.notification.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.util.concurrent.Executor;

@EnableAsync
@EnableJpaRepositories(basePackages = ApplicationLocation.REPOSITORIES)
@EnableRetry
@EnableScheduling
@EntityScan(basePackages = ApplicationLocation.ENTITIES)
@SpringBootApplication(scanBasePackages = ApplicationLocation.BASE_PACKAGE)
@EnableAutoConfiguration
@EnableTransactionManagement
public class Application {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private DataSource dataSource;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @PostConstruct
    public void init() {
        notificationService.send("System status", "Application has been started and is running.");
    }

    @Bean
    public TaskScheduler taskScheduler() {
        return new ConcurrentTaskScheduler();
    }

    @Bean
    public Executor taskExecutor() {
        return new SimpleAsyncTaskExecutor();
    }
}
