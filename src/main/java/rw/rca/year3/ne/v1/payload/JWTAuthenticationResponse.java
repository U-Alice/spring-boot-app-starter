package rw.rca.year3.ne.v1.payload;

import lombok.Data;

@Data
public class JWTAuthenticationResponse {
    private String accessToken;
    private String tokenType = "Bearer";

    public JWTAuthenticationResponse(String accessToken){
        this.accessToken = accessToken;
    }
}
