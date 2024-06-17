package rw.rca.year3.ne.v1.dtos;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Data
public class VerifyEmailDTO {
    @Email
    String email;

    @NotEmpty
    String activationCode;
}
