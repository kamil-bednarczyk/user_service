package sa.common.axon;

import org.axonframework.test.aggregate.AggregateTestFixture;
import org.axonframework.test.aggregate.FixtureConfiguration;
import org.junit.Before;
import org.junit.Test;
import sa.common.axon.activationLink.AccountActivationLinkAggregate;
import sa.common.axon.activationLink.command.ActivateAccountCommand;
import sa.common.axon.activationLink.command.CreateAccountActivationLinkCommand;
import sa.common.axon.activationLink.command.SendAccountActivationEmailCommand;
import sa.common.axon.activationLink.event.AccountActivatedEvent;
import sa.common.axon.activationLink.event.AccountActivationEmailSentEvent;
import sa.common.axon.activationLink.event.AccountActivationExpiredEvent;
import sa.common.axon.activationLink.event.AccountActivationLinkCreatedEvent;

import java.time.LocalDate;
import java.util.UUID;

public class AccountActivationLinkAggregateTest {

    private FixtureConfiguration<AccountActivationLinkAggregate> fixture;
    private String linkId;
    private LocalDate expirationDate = LocalDate.now().plusDays(1);

    @Before
    public void setUp() {
        linkId = UUID.randomUUID().toString();
        fixture = new AggregateTestFixture<>(AccountActivationLinkAggregate.class);
    }

    @Test
    public void testAccountActivationLinkCreated() {
        fixture.givenNoPriorActivity()
                .when(new CreateAccountActivationLinkCommand(linkId, "userId"))
                .expectEvents(new AccountActivationLinkCreatedEvent(linkId, "userId", expirationDate));
    }

    @Test
    public void testAccountActivationEmailSent() {
        fixture.given(new AccountActivationLinkCreatedEvent(linkId, "userId", expirationDate))
                .when(new SendAccountActivationEmailCommand(linkId, "userId", "email@email.com"))
                .expectEvents(new AccountActivationEmailSentEvent(linkId, "email@email.com"));
    }

    @Test
    public void testAccountActivated() {
        fixture.given(new AccountActivationLinkCreatedEvent(linkId, "userId", expirationDate))
                .when(new ActivateAccountCommand(linkId, LocalDate.now()))
                .expectEvents(new AccountActivatedEvent(linkId));
    }

    @Test
    public void testAccountNotActivated() {
        LocalDate afterExpirationDate = LocalDate.now().plusYears(1);
        fixture.given(new AccountActivationLinkCreatedEvent(linkId, "userId", expirationDate))
                .when(new ActivateAccountCommand(linkId, afterExpirationDate))
                .expectEvents(new AccountActivationExpiredEvent(linkId));
    }
}