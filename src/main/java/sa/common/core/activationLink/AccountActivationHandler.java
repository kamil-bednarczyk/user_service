package sa.common.core.activationLink;

import lombok.extern.log4j.Log4j2;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import sa.common.core.activationLink.event.AccountActivatedEvent;
import sa.common.core.activationLink.event.AccountActivationEmailSentEvent;
import sa.common.core.activationLink.event.AccountActivationLinkCreatedEvent;
import sa.common.email.AccountActivationLink;
import sa.common.email.ActivationLinkRepository;
import sa.common.email.ActivationStatus;
import sa.common.repository.UserRepository;

@Log4j2
@Component
public class AccountActivationHandler {

    private static final String ACTIVATION_ENDPOINT = "localhost:8092/users/activations/";

    private final JavaMailSender emailSender;
    private final ActivationLinkRepository activationLinkRepository;
    private final UserRepository userRepository;
    private AccountActivationLink accountActivationLink;

    public AccountActivationHandler(JavaMailSender emailSender, ActivationLinkRepository activationLinkRepository, UserRepository userRepository) {
        this.emailSender = emailSender;
        this.activationLinkRepository = activationLinkRepository;
        this.userRepository = userRepository;
    }

    @EventHandler
    public void on(AccountActivationLinkCreatedEvent event) {
        accountActivationLink = AccountActivationLink.builder()
                .id(event.getLinkId())
                .userId(event.getUserId())
                .status(ActivationStatus.NOT_ACTIVATED)
                .expirationDate(event.getExpirationDate())
                .build();

        activationLinkRepository.save(accountActivationLink);
    }

    @EventHandler
    public void on(AccountActivatedEvent event) {
        activationLinkRepository.findById(event.getLinkId()).ifPresent(link ->
        {
            userRepository.findById(link.getUserId()).ifPresent(user -> {
                user.setEnabled(true);
                userRepository.save(user);
            });
            link.setStatus(ActivationStatus.ACTIVATED);
            activationLinkRepository.save(link);
        });
    }

    @EventHandler
    public void on(AccountActivationEmailSentEvent event) {
        log.info("Sending account activation link to : " + event.getEmail());
        sendSimpleMessage(event.getEmail(), "Squad Agenda: activation link", ACTIVATION_ENDPOINT + accountActivationLink.getId());
    }

    private void sendSimpleMessage(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        emailSender.send(message);
    }
}