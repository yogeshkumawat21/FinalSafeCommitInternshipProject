package com.App.Yogesh.ServiceImplmentation;

import com.App.Yogesh.ResponseDto.UserDetailsDto;
import com.App.Yogesh.Models.Role;
import com.App.Yogesh.Models.User;
import com.App.Yogesh.Repository.*;
import com.App.Yogesh.RequestDto.UpdateUserRequestDto;
import com.App.Yogesh.ResponseDto.ApiResponseDto;
import com.App.Yogesh.ResponseDto.UserResponseDto;
import com.App.Yogesh.Services.UserService;
import com.App.Yogesh.Utilities.ValidationUtilities;
import com.App.Yogesh.config.UserContext;
import lombok.extern.slf4j.Slf4j;  // Lombok's SLF4J annotation
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j  // Lombok will automatically create a logger
@Service
public class UserServiceImplementation implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    private UserContext userContext;

    // Method to register a new user
    @Override
    public User registeruser(User user) {
        log.info("Starting registration for user with email: {}", user.getEmail());

        if (user.getEmail() == null || user.getPassword() == null) {
            log.error("Email or Password is null for registration");
            throw new IllegalArgumentException("Email and Password must be provided");
        }

        // Creating a new user object
        User newUser = new User();
        newUser.setEmail(user.getEmail());
        newUser.setFirstName(user.getFirstName());
        newUser.setLastName(user.getLastName());
        newUser.setPassword(user.getPassword());
        newUser.setBlocked(false); // Explicitly setting blocked status

        // Fetch the default role for the user
        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new RuntimeException("Role not found"));

        newUser.getRoles().add(userRole); // Adding the user role

        // Save the new user to the database
        User savedUser = userRepository.save(newUser);
        log.info("User registered successfully with email: {}", savedUser.getEmail());
        return savedUser;
    }

    // Method to get all users
    @Override
    public ResponseEntity<ApiResponseDto<?>> getAllUser() {
        log.info("Fetching all users");

        List<User> users = userRepository.findAll();

        if (users.isEmpty()) {
            log.warn("No users found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponseDto<>(
                    "No User Found", HttpStatus.NOT_FOUND.value(), null
            ));
        }


        List<UserResponseDto> userDtos = users.stream()
                .filter(user -> !Boolean.TRUE.equals(user.getBlocked()))
                .map(UserResponseDto::new)
                .collect(Collectors.toList());

        if (userDtos.isEmpty()) {
            log.warn("No  users found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponseDto<>(
                    "No  users found", HttpStatus.NOT_FOUND.value(), null
            ));
        }

        log.info("Unblocked users retrieved successfully, total count: {}", userDtos.size());
        return ResponseEntity.ok(new ApiResponseDto<>("Users retrieved successfully", HttpStatus.OK.value(), userDtos));
    }


    // Method to get user by ID
    @Override
    public ResponseEntity<ApiResponseDto<?>> getUserById(Integer id) {
        log.info("Fetching user with ID: {}", id);

        Optional<User> user = userRepository.findById(id);

        if (user.isEmpty()) {
            log.warn("User not found with ID: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponseDto<>(
                    "User Not Found With this Id", HttpStatus.NOT_FOUND.value(), null
            ));
        }

        User retrievedUser = user.get();

        // Check if the user is blocked
        if (retrievedUser.getBlocked()) {
            log.warn("User with ID: {} is blocked. " +
                    "Reason: {}", id, retrievedUser.getBlockedUnblockedReason());
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponseDto<>(
                    "User is blocked By Id: " + retrievedUser.getBlockedUnblockedBy() +"  for Reason "+ retrievedUser.getBlockedUnblockedReason(),
                    HttpStatus.OK.value(),
                    null
            ));
        }

        UserResponseDto userDto = new UserResponseDto(retrievedUser);
        log.info("User found with ID: {}", id);
        return ResponseEntity.ok(new ApiResponseDto<>(
                "User retrieved successfully", HttpStatus.OK.value(), userDto
        ));
    }

    // Method to update user details
    @Override
    public ResponseEntity<ApiResponseDto<?>> updateUser(UpdateUserRequestDto user) {

        if (!ValidationUtilities.areValidNames(user.getFirstName(),user.getLastName())) {
            log.warn("Invalid name format: {}",
                    user.getFirstName(),
                    user.getLastName());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponseDto(
                    "Invalid name format.",
                    HttpStatus.BAD_REQUEST.value(),
                    user.getFirstName()+" "+
                            user.getLastName()));
        }
        if (!ValidationUtilities.isValidGender(user.getGender())) {
            log.warn("Invalid Gemder Format");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponseDto(
                    "Invalid Gender Format",
                    HttpStatus.BAD_REQUEST.value(),
                    null));
        }
        try {
            UserDetailsDto currentUser = userContext.getCurrentUser();
            log.info("Attempting to update user with email: " +
                    "{}", currentUser.getEmail());
            // Find user by the current user email (from the JWT)
            User reqUser = userRepository.findByEmail(currentUser.getEmail());

            if (reqUser == null) {
                log.error("Invalid user with email: {}", currentUser.getEmail());
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponseDto(
                        "Invalid User", HttpStatus.NOT_FOUND.value(), null
                ));
            }

            Optional<User> user1 = userRepository.findById(reqUser.getId());
            if (user1.isEmpty()) {
                log.warn("User does not exist with id: {}", reqUser.getId());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponseDto(
                        "User Does not exist with this id", HttpStatus.BAD_REQUEST.value(), null
                ));
            }

            User oldUser = user1.get();
            // Updating user details if provided
            if (user.getFirstName() != null) {
                oldUser.setFirstName(user.getFirstName());
            }
            if (user.getLastName() != null) {
                oldUser.setLastName(user.getLastName());
            }
            if (user.getGender() != null) {
                oldUser.setGender(user.getGender());
            }

            User updatedUser = userRepository.save(oldUser);
            UserResponseDto updatedUserDto = new UserResponseDto(updatedUser);

            log.info("User updated successfully with ID: {}", updatedUser.getId());
            return ResponseEntity.ok(new ApiResponseDto<>(
                    "User Updated successfully", HttpStatus.OK.value(), updatedUserDto
            ));

        } catch (Exception e) {
            log.error("Error processing the JWT token or user update: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponseDto(
                    "Error processing the JWT token", HttpStatus.BAD_REQUEST.value(), null
            ));
        }
    }

    // Method to follow a user
    public ResponseEntity<ApiResponseDto<?>> followUser(Integer userId) {
        log.info("Attempting to follow user with ID: {}", userId);

        UserDetailsDto currentUser = userContext.getCurrentUser();
        User reqUser = userRepository.findByEmail(currentUser.getEmail());

        // Check if the requesting user exists
        if (reqUser == null) {
            log.error("User not found with email: {}", currentUser.getEmail());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponseDto<>(
                    "User not found with this token", HttpStatus.NOT_FOUND.value(), null
            ));
        }

        // Check if the user is trying to follow themselves
        if (reqUser.getId() ==(userId)) {
            log.warn("User with ID: {} attempted to follow themselves", userId);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponseDto<>(
                    "You cannot follow yourself", HttpStatus.BAD_REQUEST.value(), null
            ));
        }

        // Fetch the target user
        Optional<User> user2Opt = userRepository.findById(userId);
        if (user2Opt.isEmpty()) {
            log.warn("User not found with provided userId: {}", userId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponseDto<>(
                    "User not found with the provided userId", HttpStatus.NOT_FOUND.value(), null
            ));
        }

        User user2 = user2Opt.get();

        // Check if the target user is blocked
        if (user2.getBlocked()) {
            log.warn("Cannot follow user with ID: {} as " +
                    "they are blocked. Reason: {}",
                    userId, user2.getBlockedUnblockedReason());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiResponseDto<>(
                    "Cannot follow user as they are " +
                            "blocked.",
                    HttpStatus.FORBIDDEN.value(),
                    null
            ));
        }

        // Check if the user is already following the target user
        if (reqUser.getFollowings().contains(userId)) {
            log.warn("User with ID: {} is already following user with ID: {}", reqUser.getId(), userId);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponseDto<>(
                    "You are already following this user", HttpStatus.BAD_REQUEST.value(), null
            ));
        }

        // Adding current user as a follower to the target user (user2)
        user2.getFollowers().add(reqUser.getId());

        // Adding target user (user2) to the current user's followings list
        reqUser.getFollowings().add(userId);

        userRepository.save(reqUser);
        userRepository.save(user2);

        UserResponseDto userDto = new UserResponseDto(user2); // Response with the followed user data

        log.info("User with ID: {} successfully followed user with ID: {}", reqUser.getId(), userId);
        return ResponseEntity.ok(new ApiResponseDto<>(
                "User followed successfully", HttpStatus.OK.value(), userDto
        ));
    }

    // Method to search for users by a query
    @Override
    public ResponseEntity<ApiResponseDto<?>> searchUserByArguments(String query) {
        log.info("Searching for users with query: {}", query);

        List<User> users = userRepository.searchUser(query);

        if (users.isEmpty()) {
            log.warn("No users found matching the query: {}", query);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponseDto<>(
                    "No users found matching the search query", HttpStatus.NOT_FOUND.value(), null
            ));
        }

        List<UserResponseDto> userDtos = users.stream()
                .map(UserResponseDto::new)
                .collect(Collectors.toList());

        log.info("Users found matching query: {}. Total users: {}", query, userDtos.size());
        return ResponseEntity.ok(new ApiResponseDto<>(
                "Users retrieved successfully", HttpStatus.OK.value(), userDtos
        ));
    }

    // Method to get current user profile
    @Override
    public ResponseEntity<ApiResponseDto<?>> currentUserProfile() {
        log.info("Fetching current user profile");

        UserDetailsDto currentUser = userContext.getCurrentUser();
        User user = userRepository.findByEmail(currentUser.getEmail());

        if (user == null) {
            log.warn("User not found with email: {}", currentUser.getEmail());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponseDto<>(
                    "User Not Found with this token", HttpStatus.NOT_FOUND.value(), null
            ));
        }

        UserResponseDto userDto = new UserResponseDto(user);
        log.info("Current user profile found for email: {}", currentUser.getEmail());
        return ResponseEntity.ok(new ApiResponseDto<>(
                "User Profile Found", HttpStatus.FOUND.value(), userDto
        ));
    }

    // Method to save user token (for example, when generating JWT)
    @Override
    public void saveToken(Integer userId, String token) throws Exception {
        log.info("Saving token for user with ID: {}", userId);

        Optional<User> user1 = userRepository.findById(userId);
        if (user1.isEmpty()) {
            log.error("User not found with ID: {}", userId);
            throw new Exception("User not found with ID: " + userId);
        }

        User oldUser = user1.get();
        oldUser.setUserToken(token);
        userRepository.save(oldUser);

        log.info("Token saved successfully for user ID: {}", userId);
    }
}
