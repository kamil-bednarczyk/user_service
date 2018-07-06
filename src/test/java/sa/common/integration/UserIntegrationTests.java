package sa.common.integration;


import org.axonframework.commandhandling.gateway.CommandGateway;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import sa.common.core.user.CreateUserCommand;
import sa.common.email.ActivationLinkRepository;
import sa.common.model.dto.CreateUserDto;
import sa.common.model.dto.UserDto;
import sa.common.model.entity.User;
import sa.common.model.enums.Role;
import sa.common.repository.UserRepository;
import sa.common.service.CustomUserDetailsService;

import static org.assertj.core.api.Assertions.assertThat;

public class UserIntegrationTests extends BaseIntegrationTest {

    @Autowired
    private CommandGateway commandGateway;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ActivationLinkRepository activationLinkRepository;
    private CustomUserDetailsService customUserDetailsService;

    private CreateUserCommand createUserCommand;
    private User expectedUser;

    @Before
    public void setUp() {
        customUserDetailsService = new CustomUserDetailsService(userRepository, commandGateway);

        createUserCommand = CreateUserCommand.builder()
                .id("1234")
                .username("username")
                .password("password")
                .email("test@localhost.com")
                .isEnabled(false)
                .avatar(new byte[100])
                .role(Role.USER)
                .build();

        expectedUser = User.builder()
                .id(createUserCommand.getId())
                .username(createUserCommand.getUsername())
                .password(createUserCommand.getPassword()) //ignored due to password encryption
                .email(createUserCommand.getEmail())
                .role(createUserCommand.getRole())
                .avatar(createUserCommand.getAvatar())
                .enabled(createUserCommand.isEnabled())
                .build();
    }

    @Test
    public void testPersistNewUserAfterCreateUserCommandArrived() {

        CreateUserDto createUserDto = CreateUserDto.builder()
                .username("username")
                .password("password")
                .email("test@localhost.com")
                .avatar(new byte[100])
                .role("USER")
                .build();

        customUserDetailsService.sendCreateUserCommand(createUserDto);

        assertThat(userRepository.findByUsername(createUserCommand.getUsername()).get())
                .isNotNull()
                .isEqualToIgnoringGivenFields(expectedUser, "password", "id");
        assertThat(activationLinkRepository.findAll()).hasSize(1);
    }

    @Test
    public void testPersistUserUpdatesAfterUpdateUserCommandArrived() {

        CreateUserDto createUserDto = CreateUserDto.builder()
                .username("username")
                .password("password")
                .email("test@localhost.com")
                .avatar(new byte[100])
                .role("USER")
                .build();

        customUserDetailsService.sendCreateUserCommand(createUserDto);
        String id = userRepository.findByUsername(createUserDto.getUsername()).get().getId();

        UserDto updateUserDto = UserDto.builder()
                .id(id)
                .username("updated")
                .email("updatedemail@localhost.com")
                .role(Role.ADMIN.toString())
                .enabled(false)
                .avatar(new byte[20])
                .build();

        User expectedUpdatedUser = User.builder()
                .id(id)
                .username(updateUserDto.getUsername())
                .password(createUserDto.getPassword()) //ignored due to password encryption
                .email(updateUserDto.getEmail())
                .role(Role.valueOf(updateUserDto.getRole()))
                .avatar(updateUserDto.getAvatar())
                .build();

        customUserDetailsService.sendUpdateUserCommand(updateUserDto);

        assertThat(userRepository.findById(updateUserDto.getId()).get()).isNotNull()
                .isEqualToIgnoringGivenFields(expectedUpdatedUser, "password");
    }

    @After
    public void cleanUp() {
        userRepository.deleteAll();
    }
}