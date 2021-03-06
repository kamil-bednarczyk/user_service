package sa.common.service;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import sa.common.axon.user.CreateUserCommand;
import sa.common.axon.user.UpdateUserCommand;
import sa.common.exception.UsernameAlreadyExists;
import sa.common.model.dto.CreateUserDto;
import sa.common.model.dto.UserDto;
import sa.common.model.entity.User;
import sa.common.model.enums.Role;
import sa.common.repository.UserRepository;

import java.util.Optional;
import java.util.UUID;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final CommandGateway commandGateway;

    public CustomUserDetailsService(UserRepository userRepository, CommandGateway commandGateway) {
        this.userRepository = userRepository;
        this.commandGateway = commandGateway;
    }

    public void sendCreateUserCommand(CreateUserDto createUserDto) {
        if (userRepository.existsByUsername(createUserDto.getUsername())) {
            throw new UsernameAlreadyExists(createUserDto.getUsername());
        }
        commandGateway.send(CreateUserCommand.builder()
                .id(UUID.randomUUID().toString())
                .username(createUserDto.getUsername())
                .password(createUserDto.getPassword())
                .email(createUserDto.getEmail())
                .role(Role.valueOf(createUserDto.getRole()))
                .avatar(createUserDto.getAvatar())
                .enabled(true) // changed to true after acc activation via email link
                .build());
    }

    public void sendUpdateUserCommand(UserDto userDto) {
        commandGateway.send(UpdateUserCommand.builder()
                .id(userDto.getId())
                .username(userDto.getUsername())
                .email(userDto.getEmail())
                .role(Role.valueOf(userDto.getRole()))
                .isEnabled(userDto.isEnabled())
                .avatar(userDto.getAvatar())
                .build());
    }

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsername(username);
        if (!user.isPresent()) {
            throw new UsernameNotFoundException(username);
        }
        return user.get();
    }

    public static UserDto convertToDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().toString())
                .avatar(user.getAvatar())
                .enabled(user.isEnabled())
                .build();
    }
}