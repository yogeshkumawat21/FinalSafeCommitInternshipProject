package com.App.Yogesh.Controller;

import com.App.Yogesh.RequestDto.UpdateUserRequestDto;
import com.App.Yogesh.ResponseDto.ApiResponseDto;
import com.App.Yogesh.Services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User API")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @Operation(summary = "Get All Users")
    @GetMapping
    public ResponseEntity<ApiResponseDto<?>> getUsers() {
        log.info("Fetching all users");
        return userService.getAllUser();
    }

    @Operation(summary = "Get Specific User by ID")
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponseDto<?>> getUserById(@PathVariable("userId") Integer id) {
        log.info("Fetching user with ID: {}", id);
        return userService.getUserById(id);
    }

    @Operation(summary = "Update User")
    @PutMapping
    public ResponseEntity<ApiResponseDto<?>> updateUser(@RequestBody UpdateUserRequestDto updateUserRequestDto) {
        log.info("Updating user with details: {}", updateUserRequestDto);
        return userService.updateUser(updateUserRequestDto);
    }

    @Operation(summary = "Follow User by ID")
    @PutMapping("/follow/{userId2}")
    public ResponseEntity<ApiResponseDto<?>> followUserHandler(@PathVariable Integer userId2) throws Exception {
        log.info("Following user with ID: {}", userId2);
        return userService.followUser(userId2);
    }

    @Operation(summary = "Search Users")
    @GetMapping("/search")
    public ResponseEntity<ApiResponseDto<?>> searchUserbyArguments(@RequestParam("query") String query) {
        log.info("Searching users with query: {}", query);
        return userService.searchUserByArguments(query);
    }

    @Operation(summary = "Get Current User Profile")
    @GetMapping("/profile")
    public ResponseEntity<ApiResponseDto<?>> getUserFromToken() throws Exception {
        log.info("Fetching current user profile");
        return userService.currentUserProfile();
    }
}
