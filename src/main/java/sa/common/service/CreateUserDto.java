package sa.common.service;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class CreateUserDto {

    @NotEmpty
    private String username;
    @NotEmpty
    private String password;
    @NotEmpty
    private String email;
    @NotEmpty
    private String role;
}
