package sa.common.email;

import org.axonframework.eventhandling.EventHandler;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import sa.common.core.UserCreatedEvent;

import java.time.Duration;
import java.time.LocalDate;
import java.util.UUID;

@Component
public class EmailService {

    private static final Integer DEFAULT_ACTIVATION_LINK_DURATION_HOURS = 48;

    private final JavaMailSender emailSender;
    private final ActivationLinkRepository activationLinkRepository;

    public EmailService(JavaMailSender emailSender, ActivationLinkRepository activationLinkRepository) {
        this.emailSender = emailSender;
        this.activationLinkRepository = activationLinkRepository;
    }

    @EventHandler
    public void on(UserCreatedEvent event) {
        LocalDate expirationDate = LocalDate.now().plus(Duration.ofHours(DEFAULT_ACTIVATION_LINK_DURATION_HOURS));

        activationLinkRepository.save(ActivationLink.builder()
                .id(UUID.randomUUID().toString())
                .userId(event.getId())
                .status(ActivationStatus.NOT_ACTIVATED)
                .expirationDate(expirationDate)
                .build());

        sendSimpleMessage(event.getEmail(), "Squad Agenda: activation link", "TODO");
    }

    private void sendSimpleMessage(String to, String subject, String text) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        emailSender.send(message);
    }
}