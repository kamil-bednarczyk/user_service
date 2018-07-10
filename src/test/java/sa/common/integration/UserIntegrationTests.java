package sa.common.integration;


import org.axonframework.commandhandling.gateway.CommandGateway;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import sa.common.axon.user.CreateUserCommand;
import sa.common.email.ActivationLinkRepository;
import sa.common.model.dto.CreateUserDto;
import sa.common.model.dto.UserDto;
import sa.common.model.entity.User;
import sa.common.model.enums.Role;
import sa.common.repository.UserRepository;
import sa.common.service.CustomUserDetailsService;

import java.util.ArrayList;

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
                .enabled(true)
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

        User actualUser = userRepository.findByUsername(createUserCommand.getUsername()).get();
        assertThat(actualUser)
                .isNotNull()
                .isEqualToIgnoringGivenFields(expectedUser, "password", "id");
        assertThat(activationLinkRepository.findAll()).hasSize(1);

        assertThat(actualUser.isEnabled()).isEqualTo(true);  //TODO should be false via email link registration but is true for development process
        assertThat(actualUser.isAccountNonExpired()).isEqualTo(true);
        assertThat(actualUser.isAccountNonLocked()).isEqualTo(true);
        assertThat(actualUser.isCredentialsNonExpired()).isEqualTo(true);
        assertThat(actualUser.getAuthorities()).isEqualTo(new ArrayList<>());
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
                .enabled(true)
                .avatar(new byte[20])
                .build();

        User expectedUpdatedUser = User.builder()
                .id(id)
                .username(updateUserDto.getUsername())
                .password(createUserDto.getPassword()) //ignored due to password encryption
                .email(updateUserDto.getEmail())
                .enabled(true)
                .role(Role.valueOf(updateUserDto.getRole()))
                .avatar(updateUserDto.getAvatar())
                .build();

        customUserDetailsService.sendUpdateUserCommand(updateUserDto);

        User actualUpdatedUser = userRepository.findById(updateUserDto.getId()).get();

        assertThat(actualUpdatedUser).isNotNull().isEqualToIgnoringGivenFields(expectedUpdatedUser, "password");
        assertThat(actualUpdatedUser.isEnabled()).isEqualTo(true);
        assertThat(actualUpdatedUser.isAccountNonExpired()).isEqualTo(true);
        assertThat(actualUpdatedUser.isAccountNonLocked()).isEqualTo(true);
        assertThat(actualUpdatedUser.isCredentialsNonExpired()).isEqualTo(true);
        assertThat(actualUpdatedUser.getAuthorities()).isEqualTo(new ArrayList<>());
    }

    @After
    public void cleanUp() {
        activationLinkRepository.deleteAll();
        userRepository.deleteAll();
    }
}