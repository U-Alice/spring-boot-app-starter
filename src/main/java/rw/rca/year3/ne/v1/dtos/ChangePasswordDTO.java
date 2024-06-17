package rw.rca.year3.ne.v1.dtos;

import lombok.Data;
import rw.rca.year3.ne.v1.security.ValidPassword;

import javax.validation.constraints.NotBlank;

@Data
public class ChangePasswordDTO {
    @NotBlank
    private String currentPassword;

    @NotBlank
    @ValidPassword
    private String newPassword;
}
