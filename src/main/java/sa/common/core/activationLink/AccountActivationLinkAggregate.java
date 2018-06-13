package sa.common.core.activationLink;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.AggregateIdentifier;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.spring.stereotype.Aggregate;
import sa.common.core.activationLink.command.ActivateAccountCommand;
import sa.common.core.activationLink.command.CreateAccountActivationLinkCommand;
import sa.common.core.activationLink.command.SendAccountActivationEmailCommand;
import sa.common.core.activationLink.event.AccountActivatedEvent;
import sa.common.core.activationLink.event.AccountActivationEmailSentEvent;
import sa.common.core.activationLink.event.AccountActivationExpiredEvent;
import sa.common.core.activationLink.event.AccountActivationLinkCreatedEvent;
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
        apply(new AccountActivationEmailSentEvent(cmd.getLinkId(), cmd.getUserId(), cmd.getEmail()));
    }

    @CommandHandler
    public void handle(ActivateAccountCommand cmd) {
        if (cmd.getWhen().isAfter(expirationDate)) {
            apply(new AccountActivationExpiredEvent(this.linkId));
        } else {
            apply(new AccountActivatedEvent(cmd.getLinkId(), this.userId));
        }
    }

    @EventSourcingHandler
    public void on(AccountActivationLinkCreatedEvent event) {
        this.linkId = event.getLinkId();
        this.userId = event.getUserId();
        this.expirationDate = event.getExpirationDate();
    }

    @EventHandler
    public void on(AccountActivatedEvent event) {
        this.linkId = event.getLinkId();
        this.status = ActivationStatus.ACTIVATED;
    }
}