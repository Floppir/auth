package com.sigma.auth.payload.response;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TokenRefreshResponse {
    private String accessToken;
    private String refreshToken;

    public TokenRefreshResponse(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
