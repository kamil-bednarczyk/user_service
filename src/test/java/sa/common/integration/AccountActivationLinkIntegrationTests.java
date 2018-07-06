package sa.common.integration;

import lombok.extern.log4j.Log4j2;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import sa.common.core.activationLink.command.ActivateAccountCommand;
import sa.common.core.activationLink.command.CreateAccountActivationLinkCommand;
import sa.common.email.AccountActivationLink;
import sa.common.email.ActivationLinkRepository;
import sa.common.email.ActivationStatus;
import sa.common.repository.UserRepository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Java6Assertions.assertThat;

@Log4j2
public class AccountActivationLinkIntegrationTests extends BaseIntegrationTest {

    @Autowired
    private CommandGateway commandGateway;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ActivationLinkRepository activationLinkRepository;

    @Test
    public void sendAccountActivationLinkCommand() {

        String linkId = UUID.randomUUID().toString();

        AccountActivationLink expectedActivationLink = AccountActivationLink.builder()
                .id(linkId)
                .userId("eventId")
                .expirationDate(LocalDate.now().plusDays(1))
                .status(ActivationStatus.NOT_ACTIVATED)
                .build();

        commandGateway.send(new CreateAccountActivationLinkCommand(linkId, "eventId"));
        assertThat(activationLinkRepository.findById(linkId)).isEqualTo(Optional.of(expectedActivationLink));

        commandGateway.send(new ActivateAccountCommand(linkId, LocalDate.now()));
        assertThat(activationLinkRepository.findById(linkId).get().getStatus()).isEqualTo(ActivationStatus.ACTIVATED);
    }

    @Test
    public void sendAccountActivationLinkCommand2() {

        String linkId = UUID.randomUUID().toString();

        AccountActivationLink expectedActivationLink = AccountActivationLink.builder()
                .id(linkId)
                .userId("eventId")
                .expirationDate(LocalDate.now().plusDays(1))
                .status(ActivationStatus.NOT_ACTIVATED)
                .build();

        commandGateway.send(new CreateAccountActivationLinkCommand(linkId, "eventId"));
        assertThat(activationLinkRepository.findById(linkId)).isEqualTo(Optional.of(expectedActivationLink));

        commandGateway.send(new ActivateAccountCommand(linkId, LocalDate.now().plusYears(3)));
        assertThat(activationLinkRepository.findById(linkId).get().getStatus()).isEqualTo(ActivationStatus.EXPIRED);
    }
}