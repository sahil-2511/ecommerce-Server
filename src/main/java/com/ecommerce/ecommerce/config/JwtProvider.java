package com.ecommerce.ecommerce.config;

import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.security.Key;

@Component
public class JwtProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtProvider.class);

    // Token expiration time (24 hours)
    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 24 * 7;

    // Secret key for signing JWT
    private static final Key SECRET_KEY = Keys.hmacShaKeyFor(JWT_CONSTANT.SECRET_KEY.getBytes());

    /**
     * Generates a JWT token for a given username.
     */
    public String generateToken(Authentication auth) {
        Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
        String roles = populateAuthorities(authorities);

        return Jwts.builder()
                .setSubject(auth.getName()) // Username
                .claim("email", auth.getName()) // Assuming username is email
                .claim("authorities", roles) // User roles
                .setIssuedAt(new Date()) // Token issue date
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // Expiration
                .signWith(SECRET_KEY) // Signing key
                .compact();
    }

    /**
     * Extracts username from a JWT token.
     */
    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    /**
     * Extracts claims from a JWT token.
     */
    private Claims extractClaims(String token) {
        try {
            token = removeBearerPrefix(token);
            return Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            LOGGER.error("Error extracting claims from JWT", e);
            throw new RuntimeException("Invalid JWT token");
        }
    }

    /**
     * Extracts email from a JWT token.
     */
    public String getEmailFromJwtToken(String token) {
        return extractClaims(token).get("email", String.class);
    }

    /**
     * Converts authorities collection to a comma-separated string.
     */
    private String populateAuthorities(Collection<? extends GrantedAuthority> authorities) {
        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
    }

    /**
     * Validates a JWT token.
     */
    public boolean validateToken(String token) {
        try {
            extractClaims(token); // If parsing succeeds, the token is valid
            return true;
        } catch (Exception e) {
            LOGGER.error("Invalid JWT Token", e);
            return false;
        }
    }

    /**
     * Removes "Bearer " prefix from token if present.
     */
    private String removeBearerPrefix(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            return token.substring(7);
        }
        return token;
    }
}
