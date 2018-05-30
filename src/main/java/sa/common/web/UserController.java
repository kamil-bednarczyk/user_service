package sa.common.web;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import sa.common.kafka.KafkaSender;
import sa.common.repository.UserRepository;
import sa.common.service.CreateUserDto;
import sa.common.service.UserDto;
import sa.common.service.UserService;

import javax.validation.Valid;

@RestController
@RequestMapping("/users")
public class UserController {

    @Value("${kafka.topic.userCreated}")
    private String USER_CREATED_TOPIC;
    private Gson gson = new Gson();

    private UserRepository userRepository;
    private KafkaSender kafkaSender;

    @Autowired
    public UserController(UserRepository userRepository, KafkaSender kafkaSender) {
        this.userRepository = userRepository;
        this.kafkaSender = kafkaSender;
    }

    @GetMapping("/{id}")
    public Mono<UserDto> getUser(@PathVariable("id") String id) {
        return userRepository.findById(id)
                .flatMap(UserService::convertToDto);
    }

    @GetMapping
    public Flux<UserDto> getAllUsers() {
        return userRepository.findAll().flatMap(UserService::convertToDto);
    }

    @PostMapping
    public Mono<UserDto> createUser(@RequestBody @Valid CreateUserDto createUserDto) {
        return Mono.just(createUserDto)
                .flatMap(UserService::convertToUser)
                .flatMap(user -> userRepository.save(user))
                .flatMap(UserService::convertToDto)
                .doOnNext(userDto -> kafkaSender.send(USER_CREATED_TOPIC, gson.toJson(userDto)));
    }
}