package com.sigma.auth.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "roles")
public class Role {
    @Id
    private String id;
    private ERole name;
    public Role() {
    }
    public Role(ERole name) {
        this.name = name;
    }
}
