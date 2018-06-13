package sa.common.core.user;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.saga.EndSaga;
import org.axonframework.eventhandling.saga.SagaEventHandler;
import org.axonframework.eventhandling.saga.SagaLifecycle;
import org.axonframework.eventhandling.saga.StartSaga;
import org.axonframework.spring.stereotype.Saga;
import org.springframework.beans.factory.annotation.Autowired;
import sa.common.core.activationLink.command.CreateAccountActivationLinkCommand;
import sa.common.core.activationLink.command.SendAccountActivationEmailCommand;
import sa.common.core.activationLink.event.AccountActivatedEvent;
import sa.common.core.activationLink.event.AccountActivationEmailSentEvent;
import sa.common.core.activationLink.event.AccountActivationExpiredEvent;
import sa.common.core.activationLink.event.AccountActivationLinkCreatedEvent;
import sa.common.email.ActivationStatus;

import java.util.UUID;

@Saga
public class AccountActivationManagementSaga {

    @Autowired
    private transient CommandGateway commandGateway;

    private String linkId;
    private String userId;
    private String userEmail;
    private ActivationStatus status;

    @StartSaga
    @SagaEventHandler(associationProperty = "id")
    public void on(UserCreatedEvent event) {
        this.userId = event.getId();
        this.userEmail = event.getEmail();
        String link = UUID.randomUUID().toString();
        SagaLifecycle.associateWith("linkId", link);
        commandGateway.send(new CreateAccountActivationLinkCommand(link, event.getId()));
    }

    @SagaEventHandler(associationProperty = "linkId")
    public void on(AccountActivationLinkCreatedEvent event) {
        this.linkId = event.getLinkId();
        commandGateway.send(new SendAccountActivationEmailCommand(this.linkId, event.getUserId(), this.userEmail));
    }

    @SagaEventHandler(associationProperty = "linkId")
    public void on(AccountActivationEmailSentEvent event) {
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "linkId")
    public void on(AccountActivatedEvent event) {
        this.status = ActivationStatus.ACTIVATED;
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "linkId")
    public void on(AccountActivationExpiredEvent event) {
        this.status = ActivationStatus.EXPIRED;
    }
}
