package sa.common.model.dto;

import lombok.Data;
import sa.common.annotation.ValidEmail;

import javax.validation.constraints.NotEmpty;

@Data
public class CreateUserDto {

    @NotEmpty
    private String username;
    @NotEmpty
    private String password;
    @NotEmpty
    @ValidEmail
    private String email;
    @NotEmpty
    private String role;
}
