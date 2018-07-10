package sa.common.axon.user;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.saga.EndSaga;
import org.axonframework.eventhandling.saga.SagaEventHandler;
import org.axonframework.eventhandling.saga.SagaLifecycle;
import org.axonframework.eventhandling.saga.StartSaga;
import org.axonframework.spring.stereotype.Saga;
import org.springframework.beans.factory.annotation.Autowired;
import sa.common.axon.activationLink.command.CreateAccountActivationLinkCommand;
import sa.common.axon.activationLink.command.SendAccountActivationEmailCommand;
import sa.common.axon.activationLink.event.AccountActivatedEvent;
import sa.common.axon.activationLink.event.AccountActivationEmailSentEvent;
import sa.common.axon.activationLink.event.AccountActivationExpiredEvent;
import sa.common.axon.activationLink.event.AccountActivationLinkCreatedEvent;

import java.util.UUID;

@Saga
public class AccountActivationManagementSaga {

    @Autowired
    private transient CommandGateway commandGateway;

    private String userEmail;

    @StartSaga
    @SagaEventHandler(associationProperty = "id")
    public void on(UserCreatedEvent event) {
        this.userEmail = event.getEmail();
        String link = UUID.randomUUID().toString();
        SagaLifecycle.associateWith("linkId", link);
        commandGateway.send(new CreateAccountActivationLinkCommand(link, event.getId()));
    }

    @SagaEventHandler(associationProperty = "linkId")
    public void on(AccountActivationLinkCreatedEvent event) {
        commandGateway.send(new SendAccountActivationEmailCommand(event.getLinkId(), event.getUserId(), this.userEmail));
    }

    @SagaEventHandler(associationProperty = "linkId")
    public void on(AccountActivationEmailSentEvent event) {
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "linkId")
    public void on(AccountActivatedEvent event) {
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "linkId")
    public void on(AccountActivationExpiredEvent event) {
    }
}