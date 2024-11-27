package com.App.Yogesh.config;

import com.App.Yogesh.ResponseDto.UserDetailsDto;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j  // Lombok logger
public class UserContext {

    // ThreadLocal to store current user details in a thread-safe manner
    private static final ThreadLocal<UserDetailsDto> currentUser = new ThreadLocal<>();

    // Set current user in the thread-local variable
    public void setCurrentUser(UserDetailsDto userDto) {
        currentUser.set(userDto);
        log.debug("Current user set: {}", userDto.getEmail());  // Log user setting
    }

    // Get the current user from the thread-local variable
    public UserDetailsDto getCurrentUser() {
        UserDetailsDto user = currentUser.get();
        if (user != null) {
            log.debug("Retrieved current user: {}", user.getEmail());  // Log retrieval of user
        } else {
            log.debug("No current user set");  // Log when no user is set
        }
        return user;
    }

    // Clear the current user from the thread-local variable
    public void clear() {
        log.debug("Clearing current user");  // Log clearing of the user context
        currentUser.remove();
    }
}
