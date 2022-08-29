package com.sigma.auth.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "refreshToken")
public class RefreshToken {
    @Id
    private String id;

    private String token;

    public RefreshToken() {
    }
}
