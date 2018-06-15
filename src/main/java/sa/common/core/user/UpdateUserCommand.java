package sa.common.core.user;

import lombok.Builder;
import lombok.Value;
import org.axonframework.commandhandling.TargetAggregateIdentifier;
import sa.common.model.enums.Role;

@Value
@Builder
public class UpdateUserCommand {

    @TargetAggregateIdentifier
    private final String id;

    private final String username;
    private final String email;
    private final Role role;
    private final boolean isEnabled;
}
