package sa.common.model.dto;

import lombok.*;
import sa.common.annotation.ValidEmail;

import javax.validation.constraints.NotEmpty;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private String id;
    @NotEmpty
    private String username;
    @NotEmpty
    @ValidEmail
    private String email;
    @NotEmpty
    private String role;
    private boolean enable;
    private byte[] avatar;
}
