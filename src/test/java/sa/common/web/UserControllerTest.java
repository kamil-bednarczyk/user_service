package sa.common.web;


import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import sa.common.model.dto.CreateUserDto;
import sa.common.model.dto.UserDto;
import sa.common.model.entity.User;
import sa.common.model.enums.Role;
import sa.common.repository.UserRepository;
import sa.common.service.CustomUserDetailsService;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.web.reactive.function.BodyInserters.fromObject;

public class UserControllerTest {

    private WebTestClient webTestClient;
    private UserRepository userRepository;
    private CustomUserDetailsService customUserDetailsService;
    private User createdUser;
    private UserDto userDto;
    private CreateUserDto createUserDto;

    @Before
    public void setUp() {
        userRepository = mock(UserRepository.class);
        customUserDetailsService = mock(CustomUserDetailsService.class);

        webTestClient = WebTestClient.bindToController(new UserController(userRepository, customUserDetailsService))
                .configureClient()
                .baseUrl("/users")
                .build();

        createUserDto = CreateUserDto.builder()
                .username("username")
                .email("username@email.com")
                .password("password")
                .avatar(new byte[20])
                .role("USER")
                .build();

        createdUser = User.builder()
                .id("12345")
                .username(createUserDto.getUsername())
                .password(createUserDto.getPassword())
                .email(createUserDto.getEmail())
                .role(Role.valueOf(createUserDto.getRole()))
                .enabled(false)
                .avatar(createUserDto.getAvatar())
                .build();

        userDto = UserDto.builder()
                .id(createdUser.getId())
                .username(createUserDto.getUsername())
                .email(createdUser.getEmail())
                .role(createdUser.getRole().toString())
                .enabled(createdUser.isEnabled())
                .avatar(createdUser.getAvatar())
                .build();
    }

    @Test
    public void testCreateUser() {
        when(userRepository.save(any())).thenReturn(Optional.of(createdUser));

        this.webTestClient.post().uri("/registration")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(fromObject(createUserDto))
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .exchange()
                .expectStatus().is2xxSuccessful();
    }

    @Test
    public void testGetUserById() {
        when(userRepository.findById(createdUser.getId())).thenReturn(Optional.of(createdUser));

        this.webTestClient.get().uri("/" + createdUser.getId())
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
                .expectBody()
                .jsonPath("$.id").isEqualTo(createdUser.getId())
                .jsonPath("$.username").isEqualTo("username")
                .jsonPath("$.email").isEqualTo("username@email.com")
                .jsonPath("$.role").isEqualTo("USER")
                .jsonPath("$.enabled").isEqualTo("false");
    }

    @Test
    public void testUpdateUser() {

        UserDto updateUserDto = UserDto.builder()
                .id(createdUser.getId())
                .username("updated-username")
                .email("updated@email.com")
                .role(createdUser.getRole().toString())
                .enabled(createdUser.isEnabled())
                .build();

        this.webTestClient.put()
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .body(fromObject(updateUserDto))
                .exchange()
                .expectStatus().is2xxSuccessful();
    }
}
