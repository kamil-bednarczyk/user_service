package sa.common.web;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.reactive.server.WebTestClient;
import sa.common.email.AccountActivationController;

import java.util.UUID;

import static org.mockito.Mockito.mock;

public class AccountActivationControllerTest {

    private WebTestClient webTestClient;
    private CommandGateway commandGateway;

    @Before
    public void setup() {
        commandGateway = mock(CommandGateway.class);

        webTestClient = WebTestClient.bindToController(new AccountActivationController(commandGateway))
                .configureClient()
                .baseUrl("/users/activations/")
                .build();
    }

    @Test
    public void testActivateLink() {
        String linkId = UUID.randomUUID().toString();
        webTestClient.get().uri(linkId).exchange().expectStatus().isOk();
    }
}
