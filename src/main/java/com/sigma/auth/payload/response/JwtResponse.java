package com.sigma.auth.payload.response;

import java.util.List;

public class JwtResponse {
    private String token;
    private String refreshToken;

    public JwtResponse(String accessToken, String refreshToken, String id, String username, String email, List<String> roles) {
        this.token = accessToken;
        this.refreshToken = refreshToken;
    }

    public String getAccessToken() {
        return token;
    }

    public void setAccessToken(String accessToken) {
        this.token = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
