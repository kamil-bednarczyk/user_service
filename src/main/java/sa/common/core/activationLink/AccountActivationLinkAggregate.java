package sa.common.core.activationLink;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.AggregateIdentifier;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.data.annotation.Id;
import sa.common.core.activationLink.command.ActivateAccountCommand;
import sa.common.core.activationLink.command.CreateAccountActivationLinkCommand;
import sa.common.core.activationLink.command.SendAccountActivationEmailCommand;
import sa.common.core.activationLink.event.AccountActivatedEvent;
import sa.common.core.activationLink.event.AccountActivationEmailSentEvent;
import sa.common.core.activationLink.event.AccountActivationLinkCreatedEvent;
import sa.common.email.ActivationStatus;

import java.time.LocalDate;

import static org.axonframework.commandhandling.model.AggregateLifecycle.apply;


@Aggregate
public class AccountActivationLinkAggregate {

    @Id
    @AggregateIdentifier
    private String linkId;

    private String userId;
    private LocalDate expirationDate;
    private ActivationStatus status;

    @CommandHandler
    public AccountActivationLinkAggregate(CreateAccountActivationLinkCommand cmd) {
        apply(new AccountActivationLinkCreatedEvent(cmd.getLinkId(), cmd.getUserId()));
    }

    @CommandHandler
    public void handle(ActivateAccountCommand cmd) {
        apply(new AccountActivatedEvent(cmd.getId(), this.userId));
    }

    @CommandHandler
    public void handle(SendAccountActivationEmailCommand cmd) {
        apply(new AccountActivationEmailSentEvent(cmd.getLinkId(), cmd.getUserId(), cmd.getEmail()));
    }

    @EventSourcingHandler
    public void on(AccountActivationLinkCreatedEvent event) {
        this.linkId = event.getLinkId();
        this.userId = event.getUserId();
    }

    @EventHandler
    public void on(AccountActivatedEvent event) {
        this.linkId = event.getId();
        this.status = ActivationStatus.ACTIVATED;
    }
}