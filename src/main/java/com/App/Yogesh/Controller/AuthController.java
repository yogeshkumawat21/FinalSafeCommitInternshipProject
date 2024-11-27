package com.App.Yogesh.Controller;

import com.App.Yogesh.RequestDto.CreateUserRequestDto;
import com.App.Yogesh.RequestDto.LoginRequestDto;
import com.App.Yogesh.ResponseDto.ApiResponseDto;
import com.App.Yogesh.Services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Tag(name = "SignUp&In APIs")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

 private final AuthService authService;

 // Current Login User Data
 @Operation(summary = "Get Current Login User")
 @PostMapping("/CheckUser")
 public ResponseEntity<?> getCurrentUser() {
  log.info("Fetching current logged-in user data.");
  return authService.getCurrentUserData();
 }

 // Creates a new user
 @Operation(summary = "Sign up")
 @PostMapping("/signup")
 public ResponseEntity<ApiResponseDto<?>> createUser(@Valid @RequestBody CreateUserRequestDto createUserRequestDto) throws Exception {
  log.info("Sign-up request for user: {}", createUserRequestDto);
  return authService.signUpUser(createUserRequestDto);
 }

 // Authenticates a user and returns a JWT token if successful
 @Operation(summary = "Login")
 @PostMapping("/signin")
 public ResponseEntity<ApiResponseDto<?>> signin(@RequestBody LoginRequestDto loginRequest) throws Exception {
  log.info("Login request for user: {}", loginRequest.getEmail());
  return authService.LoginUser(loginRequest);
 }

 // OAuth2 Callback for login
 @Operation(summary = "OAuth2 Callback")
 @GetMapping("/oauth2/callback")
 public ResponseEntity<ApiResponseDto<?>> oauth2Callback(Authentication authentication) throws Exception {
  log.info("OAuth2 callback triggered for authentication: {}", authentication.getName());
  return authService.LoginByOauth(authentication);
 }
}
