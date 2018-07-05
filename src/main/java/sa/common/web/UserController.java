package sa.common.web;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sa.common.model.dto.CreateUserDto;
import sa.common.model.dto.UserDto;
import sa.common.repository.UserRepository;
import sa.common.web.service.CustomUserDetailsService;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@Log4j2
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

    @GetMapping("/username/{username}")
    public Optional<UserDto> getUserByName(@PathVariable("username") String username) {
        return userRepository.findByUsername(username).map(CustomUserDetailsService::convertToDto);
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(CustomUserDetailsService::convertToDto)
                .collect(Collectors.toList());
    }

    @PostMapping("/registration")
    public void registerUser(@RequestBody @Valid CreateUserDto createUserDto) {
        customUserDetailsService.sendCreateUserCommand(createUserDto);
    }

    @PutMapping
    public void updateUser(@RequestBody @Valid UserDto userDto) {
        customUserDetailsService.sendUpdateUserCommand(userDto);
    }
}