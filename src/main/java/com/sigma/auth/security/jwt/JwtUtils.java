package com.sigma.auth.security.jwt;

import java.util.Date;

import com.sigma.auth.repository.RefreshTokenRepository;
import com.sigma.auth.security.services.UserDetailsImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.*;
import org.springframework.transaction.annotation.Transactional;

@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);
@Autowired
RefreshTokenRepository refreshTokenRepository;
    @Value("${jwtRefreshExpirationMs}")
    private Long refreshTokenDurationMs;

    @Value("${jwtRefreshSecret}")
    private String jwtRefreshSecret;

    @Value("${jwtAccessSecret}")
    private String jwtAccessSecret;

    @Value("${jwtExpirationMs}")
    private int jwtExpirationMs;

    public String generateAccessToken(UserDetailsImpl userPrincipal) {
        return generateAccessTokenFromUsername(userPrincipal.getUsername());
    }

    public String createRefreshToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + refreshTokenDurationMs))
                .signWith(SignatureAlgorithm.HS512, jwtRefreshSecret)
                .compact();
    }

    public String generateAccessTokenFromUsername(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtAccessSecret)
                .compact();
    }

    public String getUserNameFromRefreshToken(String token) {
        return Jwts.parser()
                .setSigningKey(jwtRefreshSecret)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public String getUserNameFromAccessToken(String token) {
        return Jwts.parser()
                .setSigningKey(jwtAccessSecret)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateExpiration(String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtAccessSecret).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            logger.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }

    public boolean validateRefreshExpiration(String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtRefreshSecret).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            logger.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }

    @Transactional
    public void deleteByRefreshToken(String token) {
        refreshTokenRepository.deleteRefreshTokenByToken(token);
    }
}

