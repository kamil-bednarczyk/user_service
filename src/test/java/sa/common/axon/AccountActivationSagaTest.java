package sa.common.axon;

import org.axonframework.test.saga.FixtureConfiguration;
import org.axonframework.test.saga.SagaTestFixture;
import org.junit.Before;
import org.junit.Test;
import sa.common.core.activationLink.command.CreateAccountActivationLinkCommand;
import sa.common.core.user.AccountActivationSaga;
import sa.common.core.user.UserCreatedEvent;
import sa.common.model.enums.Role;

public class AccountActivationSagaTest {

    private FixtureConfiguration fixture;

    //@Before
    public void setup(){
        fixture = new SagaTestFixture<>(AccountActivationSaga.class);
    }

   // @Test
    public void testUserAccountActivation() {

        fixture.givenNoPriorActivity()
                .whenAggregate("userId")
                .publishes(UserCreatedEvent.builder()
                        .id("userId")
                        .username("username")
                        .password("password")
                        .role(Role.USER)
                        .email("test@email.com")
                        .enabled(false)
                        .build())
                .expectActiveSagas(1)
                .expectDispatchedCommands(new CreateAccountActivationLinkCommand("id", "userId"));
    }

}
