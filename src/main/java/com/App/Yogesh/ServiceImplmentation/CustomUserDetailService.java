package com.App.Yogesh.ServiceImplmentation;

import com.App.Yogesh.Models.User;
import com.App.Yogesh.Repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class CustomUserDetailService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    // Load user details by username (email)
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Attempting to load user by username (email): {}", username);

        // Fetch user from the repository using the email (username)
        User user = userRepository.findByEmail(username);
        if (user == null) {
            log.error("User not found with email: {}", username);
            throw new UsernameNotFoundException("User not found with Email: " + username);
        }

        // Log successful user retrieval
        log.info("User found: {}", username);

        List<GrantedAuthority> authorities = new ArrayList<>();

        // If user has roles, we can add them here to the authorities list (e.g., for ROLE_USER, ROLE_ADMIN)
        // For now, we are returning an empty list of authorities.

        // Returning Spring Security's UserDetails object with username (email), password, and authorities
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), authorities);
    }
}
