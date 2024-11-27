package com.App.Yogesh.config;

import com.App.Yogesh.ResponseDto.UserDetailsDto;
import com.App.Yogesh.Models.User;
import com.App.Yogesh.Repository.BlackListedTokenRepository;
import com.App.Yogesh.Repository.UserRepository;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@Slf4j  // Lombok logger
public class JwtValidator extends OncePerRequestFilter {

    @Autowired
    private UserContext userContext; // Inject UserContext
    @Autowired
    private UserRepository userRepository; // Inject UserRepository
    @Autowired
    private BlackListedTokenRepository blacklistedTokenRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String jwt = request.getHeader(jwtConstant.JWT_HEADER);

        if (jwt != null && jwt.startsWith("Bearer ")) {
            jwt = jwt.substring(7);  // Remove 'Bearer ' prefix from token

            // Check if the token is blacklisted
            if (blacklistedTokenRepository.findByUserToken(jwt).isPresent()) {
                log.warn("Blacklisted token detected: {}", jwt);  // Log the blacklisted token attempt
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token is blacklisted");
                return;
            }

            try {
                // Validate JWT token
                if (JwtProvider.validateJwtToken(jwt)) {
                    String email = JwtProvider.getEmailFromJwtToken(jwt);
                    List<GrantedAuthority> authorities = JwtProvider.getAuthoritiesFromJwtToken(jwt);

                    // Create authentication object with authorities
                    Authentication authentication = new UsernamePasswordAuthenticationToken(email, null, authorities);
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    // Fetch user details
                    User user = userRepository.findByEmail(email);
                    if (user != null) {
                        // Check if the user is blocked
                        if (user.getBlocked()) {
                            log.warn("Blocked user attempted access: {}", email);  // Log blocked user attempt
                            response.sendError(HttpServletResponse.SC_FORBIDDEN, "User is blocked.");
                            return;
                        }

                        // Set the current user context
                        UserDetailsDto userDto = new UserDetailsDto();
                        userDto.setEmail(user.getEmail());
                        userDto.setFirstName(user.getFirstName());
                        userDto.setLastName(user.getLastName());
                        userContext.setCurrentUser(userDto);

                    } else {
                        log.warn("User not found for email: {}", email);  // Log if the user is not found
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid User");
                        return;
                    }
                } else {
                    log.error("Invalid JWT Token: {}", jwt);  // Log invalid token error
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT Token");
                    return;
                }
            } catch (JwtException e) {
                log.error("JWT validation error: {}", e.getMessage());  // Log the exception if JWT is invalid
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT validation error: " + e.getMessage());
                return;
            }
        }

        // Continue with the filter chain
        filterChain.doFilter(request, response);
    }
}
