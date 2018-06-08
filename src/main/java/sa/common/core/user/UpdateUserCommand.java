package sa.common.core.user;

import lombok.Builder;
import lombok.Value;
import org.axonframework.commandhandling.TargetAggregateIdentifier;

@Value
@Builder
public class UpdateUserCommand {

    @TargetAggregateIdentifier
    private final String id;

    private final String username;
    private final String email;
    private final String role;
    private final boolean enable;
}
