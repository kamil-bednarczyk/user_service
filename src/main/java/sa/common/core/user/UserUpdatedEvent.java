package sa.common.core.user;

import lombok.Builder;
import lombok.Value;
import sa.common.model.enums.Role;

@Value
@Builder
public class UserUpdatedEvent {

    private final String id;
    private final String username;
    private final String email;
    private final Role role;
    private final boolean enable;
    private final byte[] avatar;
}
