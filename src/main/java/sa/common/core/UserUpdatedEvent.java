package sa.common.core;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UserUpdatedEvent {

    private final String id;
    private final String username;
    private final String email;
    private final String role;
    private final boolean enable;
}
