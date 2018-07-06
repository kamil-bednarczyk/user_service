package sa.common.unit;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.junit.Before;
import org.junit.Test;
import sa.common.exception.UsernameAlreadyExists;
import sa.common.model.dto.CreateUserDto;
import sa.common.repository.UserRepository;
import sa.common.service.CustomUserDetailsService;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CustomUserDetailsServiceTest {

    UserRepository userRepository;
    CommandGateway commandGateway;
    CustomUserDetailsService customUserDetailsService;

    @Before
    public void setup() {
        userRepository = mock(UserRepository.class);
        commandGateway = mock(CommandGateway.class);
        customUserDetailsService = new CustomUserDetailsService(userRepository, commandGateway);
    }

    @Test(expected = UsernameAlreadyExists.class)
    public void testCreateNewUser_Expect_UsernameAlreadyExists() {
        CreateUserDto createUserDto = CreateUserDto.builder()
                .username("username")
                .password("password")
                .email("test@localhost.com")
                .avatar(new byte[100])
                .role("USER")
                .build();

        when(userRepository.existsByUsername(createUserDto.getUsername())).thenReturn(true);

        customUserDetailsService.sendCreateUserCommand(createUserDto);
    }
}
