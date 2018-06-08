package sa.common.core.user;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.saga.EndSaga;
import org.axonframework.eventhandling.saga.SagaEventHandler;
import org.axonframework.eventhandling.saga.StartSaga;
import org.axonframework.spring.stereotype.Saga;
import org.springframework.beans.factory.annotation.Autowired;
import sa.common.core.activationLink.command.CreateAccountActivationLinkCommand;
import sa.common.core.activationLink.command.SendAccountActivationEmailCommand;
import sa.common.core.activationLink.event.AccountActivationEmailSentEvent;
import sa.common.core.activationLink.event.AccountActivatedEvent;
import sa.common.core.activationLink.event.AccountActivationExpiredEvent;
import sa.common.core.activationLink.event.AccountActivationLinkCreatedEvent;
import sa.common.email.ActivationStatus;

import java.util.UUID;

@Saga
public class AccountActivationSaga {

    private transient CommandGateway commandGateway;

    private String linkId;
    private String userId;
    private String userEmail;
    private ActivationStatus status;

    @Autowired
    public AccountActivationSaga(CommandGateway commandGateway) {
        this.commandGateway = commandGateway;
    }

    @StartSaga
    @SagaEventHandler(associationProperty = "id")
    public void handle(UserCreatedEvent event) {
        this.userId = event.getId();
        this.linkId = UUID.randomUUID().toString();
        this.userEmail = event.getEmail();
        commandGateway.send(new CreateAccountActivationLinkCommand(this.linkId, event.getId()));
    }

    @SagaEventHandler(associationProperty = "linkId")
    public void handle(AccountActivationLinkCreatedEvent event) {
        this.status = ActivationStatus.NOT_ACTIVATED;
        commandGateway.send(new SendAccountActivationEmailCommand(event.getLinkId(), event.getUserId(), this.userEmail));
    }

    @SagaEventHandler(associationProperty = "linkId")
    public void handle(AccountActivationEmailSentEvent event){

    }

    @EndSaga
    @SagaEventHandler(associationProperty = "")
    public void handle(AccountActivatedEvent event) {
        this.status = ActivationStatus.ACTIVATED;
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "")
    public void handle(AccountActivationExpiredEvent event){
        this.status = ActivationStatus.EXPIRED;
    }

}
