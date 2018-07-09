package sa.common.integration;

import lombok.extern.log4j.Log4j2;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import sa.common.core.activationLink.command.ActivateAccountCommand;
import sa.common.core.activationLink.command.CreateAccountActivationLinkCommand;
import sa.common.core.user.CreateUserCommand;
import sa.common.email.AccountActivationLink;
import sa.common.email.ActivationLinkRepository;
import sa.common.email.ActivationStatus;
import sa.common.model.enums.Role;
import sa.common.repository.UserRepository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Java6Assertions.assertThat;

@Log4j2
@DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
public class AccountActivationLinkIntegrationTests extends BaseIntegrationTest {

    @Autowired
    private CommandGateway commandGateway;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ActivationLinkRepository activationLinkRepository;

    @Test
    public void sendAccountActivationLinkCommand_ExpectAccountActivated() {


        CreateUserCommand createUserCommand = CreateUserCommand.builder()
                .id("1234")
                .username("username")
                .password("password")
                .email("test@localhost.com")
                .isEnabled(false)
                .avatar(new byte[100])
                .role(Role.USER)
                .build();

        commandGateway.send(createUserCommand);

        String linkId = UUID.randomUUID().toString();

        AccountActivationLink expectedActivationLink = AccountActivationLink.builder()
                .id(linkId)
                .userId(createUserCommand.getId())
                .expirationDate(LocalDate.now().plusDays(1))
                .status(ActivationStatus.NOT_ACTIVATED)
                .build();

        commandGateway.send(new CreateAccountActivationLinkCommand(linkId, createUserCommand.getId()));
        assertThat(activationLinkRepository.findById(linkId)).isEqualTo(Optional.of(expectedActivationLink));

        commandGateway.send(new ActivateAccountCommand(linkId, LocalDate.now()));
        assertThat(activationLinkRepository.findById(linkId).get().getStatus()).isEqualTo(ActivationStatus.ACTIVATED);
        assertThat(userRepository.findById(expectedActivationLink.getUserId()).get().isEnabled()).isEqualTo(true);
    }

    @Test
    public void sendAccountActivationLinkCommand_ExpectAccountActivationExpired() {


        CreateUserCommand createUserCommand = CreateUserCommand.builder()
                .id("4356")
                .username("username")
                .password("password")
                .email("test@localhost.com")
                .isEnabled(false)
                .avatar(new byte[100])
                .role(Role.USER)
                .build();

        commandGateway.send(createUserCommand);

        String linkId = UUID.randomUUID().toString();

        AccountActivationLink expectedActivationLink = AccountActivationLink.builder()
                .id(linkId)
                .userId(createUserCommand.getId())
                .expirationDate(LocalDate.now().plusDays(1))
                .status(ActivationStatus.NOT_ACTIVATED)
                .build();

        commandGateway.send(new CreateAccountActivationLinkCommand(linkId, createUserCommand.getId()));
        assertThat(activationLinkRepository.findById(linkId)).isEqualTo(Optional.of(expectedActivationLink));

        commandGateway.send(new ActivateAccountCommand(linkId, LocalDate.now().plusYears(3)));
        assertThat(activationLinkRepository.findById(linkId).get().getStatus()).isEqualTo(ActivationStatus.EXPIRED);
        assertThat(userRepository.findById(expectedActivationLink.getUserId()).get().isEnabled()).isEqualTo(false);
    }
}