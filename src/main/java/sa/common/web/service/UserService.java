package sa.common.web.service;

import sa.common.model.dto.CreateUserDto;
import sa.common.model.dto.UserDto;
import sa.common.model.entity.User;
import sa.common.model.enums.Role;

import javax.validation.Valid;
import java.util.Optional;
import java.util.UUID;

public class UserService {
    public static UserDto convertToDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().toString())
                .build();
    }

    public static Optional<User> convertToUser(@Valid CreateUserDto createUserDto) {
        return Optional.of(User.builder()
                .id(UUID.randomUUID().toString())
                .username(createUserDto.getUsername())
                .email(createUserDto.getEmail())
                .password(createUserDto.getPassword())
                .enabled(true)
                .role(Role.valueOf(createUserDto.getRole()))
                .build());
    }
}
