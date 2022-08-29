package com.sigma.auth.repository;

import com.sigma.auth.models.RefreshToken;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends MongoRepository<RefreshToken, String> {


    void deleteRefreshTokenByToken (String token);

    boolean existsByToken(String token);

}
