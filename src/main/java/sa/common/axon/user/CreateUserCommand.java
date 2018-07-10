package sa.common.axon.user;

import lombok.Builder;
import lombok.Value;
import org.axonframework.commandhandling.TargetAggregateIdentifier;
import sa.common.model.enums.Role;

@Value
@Builder
public class CreateUserCommand {

    @TargetAggregateIdentifier
    private final String id;

    private final String username;
    private final String password;
    private final String email;
    private final byte[] avatar;
    private final boolean enabled;
    private final Role role;
}
