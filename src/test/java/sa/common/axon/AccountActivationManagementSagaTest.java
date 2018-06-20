package sa.common.axon;

import org.axonframework.test.saga.FixtureConfiguration;
import org.axonframework.test.saga.SagaTestFixture;
import org.junit.Before;
import org.junit.Test;
import sa.common.core.activationLink.command.CreateAccountActivationLinkCommand;
import sa.common.core.activationLink.command.SendAccountActivationEmailCommand;
import sa.common.core.activationLink.event.AccountActivatedEvent;
import sa.common.core.activationLink.event.AccountActivationExpiredEvent;
import sa.common.core.activationLink.event.AccountActivationLinkCreatedEvent;
import sa.common.core.user.AccountActivationManagementSaga;
import sa.common.core.user.UserCreatedEvent;
import sa.common.model.enums.Role;

import java.time.LocalDate;
import java.util.UUID;

public class AccountActivationManagementSagaTest {

    private FixtureConfiguration fixture;

    @Before
    public void setup() {
        fixture = new SagaTestFixture<>(AccountActivationManagementSaga.class);
    }

    @Test
    public void testUserAccountActivation() {
        UserCreatedEvent userCreatedEvent = UserCreatedEvent.builder()
                .id("12345")
                .username("username")
                .password("password")
                .role(Role.USER)
                .email("test@email.com")
                .enabled(false)
                .build();

        fixture.registerIgnoredField(CreateAccountActivationLinkCommand.class, "linkId")
                .givenNoPriorActivity()
                .whenAggregate(userCreatedEvent.getId())
                .publishes(userCreatedEvent)
                .expectActiveSagas(1)
                .expectDispatchedCommands(new CreateAccountActivationLinkCommand(
                        UUID.randomUUID().toString(),
                        userCreatedEvent.getId()));
    }

    @Test
    public void testAccountActivationLinkSent() {
        final String linkId = UUID.randomUUID().toString();
        LocalDate expirationDate = LocalDate.now().plusDays(1);
        UserCreatedEvent userCreatedEvent = UserCreatedEvent.builder()
                .id("12345")
                .username("username")
                .password("password")
                .role(Role.USER)
                .email("test@email.com")
                .enabled(false)
                .build();

        fixture.givenAggregate(userCreatedEvent.getId()).published(userCreatedEvent)
                .whenAggregate(linkId)
                .publishes(new AccountActivationLinkCreatedEvent(linkId, userCreatedEvent.getId(), expirationDate))
                .expectActiveSagas(1)
                .expectDispatchedCommands(
                        new SendAccountActivationEmailCommand(linkId, userCreatedEvent.getId(), userCreatedEvent.getEmail()));
    }

    @Test
    public void testAccountActivatedAfterLinkClicked() {
        String linkId = UUID.randomUUID().toString();
        UserCreatedEvent userCreatedEvent = UserCreatedEvent.builder()
                .id("12345")
                .username("username")
                .password("password")
                .role(Role.USER)
                .email("test@email.com")
                .enabled(false)
                .build();

        fixture.givenAggregate(userCreatedEvent.getId()).published(userCreatedEvent)
                .andThenAggregate(linkId).published(new AccountActivationLinkCreatedEvent(linkId, userCreatedEvent.getId(), LocalDate.now()))
                .whenAggregate(linkId).publishes(new AccountActivatedEvent(linkId, userCreatedEvent.getId()))
                .expectActiveSagas(1)
                .expectNoDispatchedCommands();
    }

    @Test
    public void testAccountNotActivatedAfterLinkExpired() {
        String linkId = UUID.randomUUID().toString();
        UserCreatedEvent userCreatedEvent = UserCreatedEvent.builder()
                .id("12345")
                .username("username")
                .password("password")
                .role(Role.USER)
                .email("test@email.com")
                .enabled(false)
                .build();

        fixture.givenAggregate(userCreatedEvent.getId()).published(userCreatedEvent)
                .andThenAggregate(linkId).published(new AccountActivationLinkCreatedEvent(linkId, "userId", LocalDate.now()))
                .whenAggregate(linkId).publishes(new AccountActivationExpiredEvent(linkId))
                .expectActiveSagas(1)
                .expectNoDispatchedCommands();
    }
}