package sa.common.web;

import com.google.gson.Gson;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import sa.common.core.CreateUserCommand;
import sa.common.core.UpdateUserCommand;
import sa.common.model.dto.CreateUserDto;
import sa.common.model.dto.UserDto;
import sa.common.model.enums.Role;
import sa.common.repository.UserRepository;
import sa.common.web.service.UserService;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserController {

    private Gson gson;
    private UserRepository userRepository;
    private CommandGateway commandGateway;

    @Autowired
    public UserController(UserRepository userRepository, CommandGateway commandGateway) {
        this.userRepository = userRepository;
        this.commandGateway = commandGateway;
        this.gson = new Gson();
    }

    @GetMapping("/{id}")
    public Optional<UserDto> getUser(@PathVariable("id") String id) {
        return userRepository.findById(id).map(UserService::convertToDto);
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserService::convertToDto)
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
                .enabled(true)
                .build());
    }

    @PutMapping
    public void updateUser(@RequestBody @Valid UserDto userDto) {
        commandGateway.send(UpdateUserCommand.builder()
                .id(userDto.getId())
                .username(userDto.getUsername())
                .email(userDto.getEmail())
                .role(userDto.getRole())
                .enable(userDto.isEnable())
                .build());

    }
}