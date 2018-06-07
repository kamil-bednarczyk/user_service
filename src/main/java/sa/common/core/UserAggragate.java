package sa.common.core;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.AggregateIdentifier;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.spring.stereotype.Aggregate;
import sa.common.model.enums.Role;

import static org.axonframework.commandhandling.model.AggregateLifecycle.apply;

@Aggregate
public class UserAggragate {

    @AggregateIdentifier
    private String id;

    private String username;
    private String password;
    private String email;
    private boolean enabled;
    private Role role;

    @SuppressWarnings("unused")
    protected UserAggragate() {
    }

    @CommandHandler
    public UserAggragate(CreateUserCommand cmd) {
        apply(UserCreatedEvent.builder()
                .id(cmd.getId())
                .username(cmd.getUsername())
                .password(cmd.getPassword())
                .email(cmd.getEmail())
                .role(cmd.getRole())
                .enabled(cmd.isEnabled())
                .build());
    }

    @CommandHandler
    public void handle(UpdateUserCommand cmd) {
        apply(UserUpdatedEvent.builder()
                .id(cmd.getId())
                .username(cmd.getUsername())
                .email(cmd.getEmail())
                .role(cmd.getRole())
                .enable(cmd.isEnable())
                .build());
    }

    @EventSourcingHandler
    public void on(UserCreatedEvent event) {
        this.id = event.getId();
        this.username = event.getUsername();
        this.email = event.getEmail();
        this.password = event.getPassword();
        this.role = event.getRole();
        this.enabled = event.isEnabled();
    }

    @EventHandler
    public void on(UserUpdatedEvent event) {
        this.username = event.getUsername();
        this.email = event.getEmail();
        this.role = Role.valueOf(event.getRole());
        this.enabled = event.isEnable();
    }
}