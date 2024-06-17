package rw.rca.year3.ne.v1.dtos;


import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class SignInDTO {

    @NotBlank
    private  String email;

    @NotBlank
    private  String password;
}

