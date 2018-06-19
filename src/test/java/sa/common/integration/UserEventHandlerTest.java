package sa.common.integration;


import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit.GreenMailRule;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;
import com.icegreen.greenmail.util.ServerSetupTest;
import lombok.extern.log4j.Log4j2;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import sa.common.core.user.CreateUserCommand;
import sa.common.core.user.UpdateUserCommand;
import sa.common.email.ActivationLinkRepository;
import sa.common.model.entity.User;
import sa.common.model.enums.Role;
import sa.common.repository.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;

@Log4j2
@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
public class UserEventHandlerTest {

    @Autowired
    private CommandGateway commandGateway;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ActivationLinkRepository activationLinkRepository;

    private CreateUserCommand createUserCommand;
    private User expectedUser;

    public GreenMail greenMail;

    @Before
    public void setUp() {

        greenMail = new GreenMail(ServerSetupTest.SMTP);
        greenMail.start();

        createUserCommand = CreateUserCommand.builder()
                .id("1234")
                .username("username")
                .password("password")
                .email("test@localhost.com")
                .isEnabled(false)
                .role(Role.USER)
                .build();

        expectedUser = User.builder()
                .id(createUserCommand.getId())
                .username(createUserCommand.getUsername())
                .password(createUserCommand.getId()) //ignored due to password encryption
                .email(createUserCommand.getEmail())
                .role(createUserCommand.getRole())
                .enabled(createUserCommand.isEnabled())
                .build();
    }

    @After
    public void cleanUp() {
        greenMail.stop();
    }

    @Test
    public void testPersistNewUserAfterEventEmitted() {
        commandGateway.send(createUserCommand);

        assertThat(userRepository.findById(createUserCommand.getId()).get())
                .isNotNull()
                .isEqualToIgnoringGivenFields(expectedUser, "password");
        assertThat(activationLinkRepository.findAll()).hasSize(1);
    }

    @Test
    public void testPersistUserUpdatesAfterEventEmitted() {

        UpdateUserCommand updateUserCommand = UpdateUserCommand.builder()
                .id("1234")
                .username("updated")
                .email("updatedemail@localhost.com")
                .role(Role.ADMIN)
                .build();

        commandGateway.send(updateUserCommand);

        assertThat(userRepository.findById(updateUserCommand.getId()).get())
                .isNotNull()
                .isEqualToIgnoringGivenFields(updateUserCommand, "password");
    }
}