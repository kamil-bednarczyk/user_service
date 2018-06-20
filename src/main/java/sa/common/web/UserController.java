package sa.common.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sa.common.model.dto.CreateUserDto;
import sa.common.model.dto.UserDto;
import sa.common.repository.UserRepository;
import sa.common.web.service.CustomUserDetailsService;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserRepository userRepository;
    private final CustomUserDetailsService customUserDetailsService;

    @Autowired
    public UserController(UserRepository userRepository,
                          CustomUserDetailsService customUserDetailsService) {
        this.userRepository = userRepository;
        this.customUserDetailsService = customUserDetailsService;
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
        customUserDetailsService.sendCreateUserCommand(createUserDto);
    }

    @PutMapping
    public void updateUser(@RequestBody @Valid UserDto userDto) {
        customUserDetailsService.sendUpdateUserCommand(userDto);
    }

}