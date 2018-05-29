package sa.common.service;

import lombok.Value;

import javax.validation.constraints.NotEmpty;

@Value
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
