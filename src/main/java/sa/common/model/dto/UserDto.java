package sa.common.model.dto;

import lombok.Builder;
import lombok.Data;
import sa.common.annotation.ValidEmail;

import javax.validation.constraints.NotEmpty;

@Data
@Builder
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
}
