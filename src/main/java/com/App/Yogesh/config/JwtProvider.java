package com.App.Yogesh.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.App.Yogesh.config.jwtConstant.SECRET_KEY;

@Slf4j  // Lombok logger
public class JwtProvider {

    // Define the expiration time for JWT tokens (1 day)
    private static final long EXPIRATION_TIME = 86400000;

    // Method to generate JWT token
    public static String generateToken(String email, List<GrantedAuthority> authorities) {
        Claims claims = Jwts.claims().setSubject(email);

        // Collect authorities as String[]
        String[] authorityArray = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .toArray(String[]::new);

        // Add authorities to claims
        claims.put("authorities", authorityArray);

        String token = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();

        log.info("Generated JWT token for email: {}", email); // Log token generation
        return token;
    }

    // Method to extract email from JWT token
    public static String getEmailFromJwtToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    // Method to extract authorities from JWT token
    public static List<GrantedAuthority> getAuthoritiesFromJwtToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();

        List<GrantedAuthority> authorities = new ArrayList<>();
        Object authoritiesClaim = claims.get("authorities");

        // Convert authorities claim to GrantedAuthority list
        if (authoritiesClaim instanceof String[]) {
            for (String authority : (String[]) authoritiesClaim) {
                authorities.add(new SimpleGrantedAuthority(authority));
            }
        } else if (authoritiesClaim instanceof List) {
            for (Object authority : (List<?>) authoritiesClaim) {
                if (authority instanceof String) {
                    authorities.add(new SimpleGrantedAuthority((String) authority));
                }
            }
        } else {
            log.warn("Authorities claim is of unexpected type: {}", authoritiesClaim.getClass()); // Log unexpected type
        }

        return authorities;
    }

    // Method to validate JWT token
    public static boolean validateJwtToken(String token) {
        try {
            Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token);
            log.info("Token validation successful.");
            return true; // Token is valid
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Invalid JWT token: {}", token, e); // Log the error with exception details
            return false; // Token is invalid
        }
    }
}
