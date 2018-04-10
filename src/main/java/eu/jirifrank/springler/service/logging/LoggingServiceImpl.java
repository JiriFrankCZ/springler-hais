package eu.jirifrank.springler.service.logging;

import eu.jirifrank.springler.api.entity.Log;
import eu.jirifrank.springler.service.persistence.LogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class LoggingServiceImpl implements LoggingService{

    @Autowired
    private LogRepository logRepository;

    @Override
    @Async
    public void log(String message) {
        Log log = Log.builder()
                .message(message)
                .created(new Date())
                .build();

        logRepository.save(log);
    }
}
