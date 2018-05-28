package sa.common.user_service.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import sa.common.user_service.model.enums.Role;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class User {

    @Id
    private String id;
    private String username;
    private String password;
    private String email;
    private boolean enabled;
    private Role role;
}
