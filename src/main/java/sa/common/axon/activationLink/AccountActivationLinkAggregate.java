package sa.common.axon.activationLink;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.AggregateIdentifier;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.spring.stereotype.Aggregate;
import sa.common.axon.activationLink.command.ActivateAccountCommand;
import sa.common.axon.activationLink.command.CreateAccountActivationLinkCommand;
import sa.common.axon.activationLink.command.SendAccountActivationEmailCommand;
import sa.common.axon.activationLink.event.AccountActivatedEvent;
import sa.common.axon.activationLink.event.AccountActivationEmailSentEvent;
import sa.common.axon.activationLink.event.AccountActivationExpiredEvent;
import sa.common.axon.activationLink.event.AccountActivationLinkCreatedEvent;
import sa.common.email.ActivationStatus;

import java.time.LocalDate;

import static org.axonframework.commandhandling.model.AggregateLifecycle.apply;

@Aggregate
public class AccountActivationLinkAggregate {

    private static final LocalDate DEFAULT_EXPIRATION_PERIOD_IN_DAYS = LocalDate.now().plusDays(1);

    @AggregateIdentifier
    private String linkId;

    private String userId;
    private LocalDate expirationDate;
    private ActivationStatus status;

    @SuppressWarnings("unused")
    protected AccountActivationLinkAggregate() {
    }

    @CommandHandler
    public AccountActivationLinkAggregate(CreateAccountActivationLinkCommand cmd) {
        apply(new AccountActivationLinkCreatedEvent(cmd.getLinkId(), cmd.getUserId(), DEFAULT_EXPIRATION_PERIOD_IN_DAYS));
    }

    @CommandHandler
    public void handle(SendAccountActivationEmailCommand cmd) {
        apply(new AccountActivationEmailSentEvent(cmd.getLinkId(), cmd.getEmail()));
    }

    @CommandHandler
    public void handle(ActivateAccountCommand cmd) {
        if (cmd.getWhen().isBefore(expirationDate)) {
            apply(new AccountActivatedEvent(cmd.getLinkId()));
        } else {
            apply(new AccountActivationExpiredEvent(cmd.getLinkId()));
        }
    }

    @EventSourcingHandler
    public void on(AccountActivationLinkCreatedEvent event) {
        this.status = ActivationStatus.NOT_ACTIVATED;
        this.linkId = event.getLinkId();
        this.userId = event.getUserId();
        this.expirationDate = event.getExpirationDate();
    }

    @EventHandler
    public void on(AccountActivatedEvent event) {
        this.linkId = event.getLinkId();
        this.status = ActivationStatus.ACTIVATED;
    }

    @EventHandler
    public void on(AccountActivationExpiredEvent event) {
        this.linkId = event.getLinkId();
        this.status = ActivationStatus.EXPIRED;
    }
}