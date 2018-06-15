package sa.common.core.user;

import lombok.extern.log4j.Log4j2;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import sa.common.model.entity.User;
import sa.common.model.enums.Role;
import sa.common.repository.UserRepository;

@Log4j2
@Component
public class UserEventHandler {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    public UserEventHandler(UserRepository userRepository, PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.encoder = encoder;
    }

    @EventHandler
    public void on(UserCreatedEvent event) {
        userRepository.save(User.builder()
                .id(event.getId())
                .username(event.getUsername())
                .email(event.getEmail())
                .password(encoder.encode(event.getPassword()))
                .role(event.getRole())
                .enabled(event.isEnabled())
                .build());
    }

    @EventHandler
    public void on(UserUpdatedEvent event) {
        userRepository.findById(event.getId())
                .map(user -> update(user, event))
                .map(userRepository::save);
    }

    private static User update(User user, UserUpdatedEvent event) {
        user.setUsername(event.getUsername());
        user.setEmail(event.getEmail());
        user.setRole(Role.valueOf(event.getRole()));
        user.setEnabled(event.isEnable());
        return user;
    }
}