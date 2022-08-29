package com.sigma.auth.payload.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignUpResponse {
    private String accessToken;

    private String refreshToken;

    public SignUpResponse(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
