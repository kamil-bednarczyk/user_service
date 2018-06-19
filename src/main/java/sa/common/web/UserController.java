package sa.common.web;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sa.common.core.user.CreateUserCommand;
import sa.common.core.user.UpdateUserCommand;
import sa.common.model.dto.CreateUserDto;
import sa.common.model.dto.UserDto;
import sa.common.model.enums.Role;
import sa.common.repository.UserRepository;
import sa.common.web.service.CustomUserDetailsService;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/users")
public class UserController {

    private UserRepository userRepository;
    private CommandGateway commandGateway;

    @Autowired
    public UserController(UserRepository userRepository, CommandGateway commandGateway) {
        this.userRepository = userRepository;
        this.commandGateway = commandGateway;
    }

    @GetMapping("/{id}")
    public Optional<UserDto> getUser(@PathVariable("id") String id) {
        return userRepository.findById(id).map(CustomUserDetailsService::convertToDto);
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(CustomUserDetailsService::convertToDto)
                .collect(Collectors.toList());
    }

    @PostMapping
    public void createUser(@RequestBody @Valid CreateUserDto createUserDto) {
         commandGateway.send(CreateUserCommand.builder()
                .id(UUID.randomUUID().toString())
                .username(createUserDto.getUsername())
                .password(createUserDto.getPassword())
                .email(createUserDto.getEmail())
                .role(Role.valueOf(createUserDto.getRole()))
                .isEnabled(false) // changed to true after acc activation via email link
                .build());
    }

    @PutMapping
    public void updateUser(@RequestBody @Valid UserDto userDto) {
        commandGateway.send(UpdateUserCommand.builder()
                .id(userDto.getId())
                .username(userDto.getUsername())
                .email(userDto.getEmail())
                .role(Role.valueOf(userDto.getRole()))
                .isEnabled(userDto.isEnable())
                .build());
    }
}