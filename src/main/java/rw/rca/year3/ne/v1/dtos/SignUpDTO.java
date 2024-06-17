package rw.rca.year3.ne.v1.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import rw.rca.year3.ne.v1.enums.EGender;
import rw.rca.year3.ne.v1.enums.ERole;
import rw.rca.year3.ne.v1.models.LocationAddress;
import rw.rca.year3.ne.v1.security.ValidPassword;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.UUID;

@Getter
@Data
@AllArgsConstructor
public class SignUpDTO {

    @Email
    private  String email;

    @NotBlank
    private  String firstName;

    @NotBlank
    private  String lastName;

    @Pattern(regexp = "[0-9]{16}")
    private String nationalId;

    @NotBlank
    @Pattern(regexp = "[0-9]{9,12}", message = "Your phone is not a valid tel we expect 2507***, or 07*** or 7***")
    private  String mobile;

    private EGender gender;

    @NotNull
    private ERole role = ERole.HEAD_TEACHER;

    @ValidPassword
    private  String password;

}

