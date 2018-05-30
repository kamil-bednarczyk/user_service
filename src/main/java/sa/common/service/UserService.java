package sa.common.service;

import reactor.core.publisher.Mono;
import sa.common.model.entity.User;
import sa.common.model.enums.Role;
import sa.common.model.dto.CreateUserDto;
import sa.common.model.dto.UserDto;

import javax.validation.Valid;
import java.util.UUID;

public class UserService {
    public static Mono<UserDto> convertToDto(User user) {
        return Mono.just(UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().toString())
                .build());
    }

    public static Mono<User> convertToUser(@Valid CreateUserDto createUserDto) {
        return Mono.just(User.builder()
                .id(UUID.randomUUID().toString())
                .username(createUserDto.getUsername())
                .email(createUserDto.getEmail())
                .password(createUserDto.getPassword())
                .enabled(true)
                .role(Role.valueOf(createUserDto.getRole()))
                .build());
    }

    public static Mono<User> update(User user, UserDto userDto) {
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setRole(Role.valueOf(userDto.getRole()));
        return Mono.just(user);
    }
}
