package rw.rca.year3.ne.v1.dtos;

import lombok.Getter;
import rw.rca.year3.ne.v1.security.ValidPassword;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
public class ResetPasswordDto {

    @NotBlank
    @Email
    String email;

    @NotBlank
    String activationCode;

    @ValidPassword
    String password;

}
