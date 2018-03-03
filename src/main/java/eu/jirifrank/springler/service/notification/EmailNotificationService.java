package eu.jirifrank.springler.service.notification;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailNotificationService implements NotificationService {

    private static final String SUBJECT_PREFIX = "Springler - ";

    @Value("${notifications.email}")
    private String[] emailAddresses;

    @Autowired
    private JavaMailSender emailSender;

    @Override
    public void send(String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(emailAddresses);
        message.setSubject(SUBJECT_PREFIX + subject);
        message.setText(text);
        //emailSender.send(message);
    }
}
