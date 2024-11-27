package com.App.Yogesh.ServiceImplmentation;
import com.App.Yogesh.ResponseDto.UserDetailsDto;
import com.App.Yogesh.Models.Role;
import com.App.Yogesh.Models.User;
import com.App.Yogesh.Repository.RoleRepository;
import com.App.Yogesh.Repository.UserRepository;
import com.App.Yogesh.RequestDto.CreateUserRequestDto;
import com.App.Yogesh.RequestDto.LoginRequestDto;
import com.App.Yogesh.ResponseDto.ApiResponseDto;
import com.App.Yogesh.ResponseDto.UserResponseDto;
import com.App.Yogesh.Services.AuthService;
import com.App.Yogesh.Services.UserService;
import com.App.Yogesh.Utilities.ValidationUtilities;
import com.App.Yogesh.config.JwtProvider;
import com.App.Yogesh.config.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class AuthServiceImplementation implements AuthService {

    @Autowired
    UserContext userContext;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    private CustomUserDetailService customUserDetailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    UserService userService;

    // Fetch the current user data
    @Override
    public ResponseEntity<ApiResponseDto<?>> getCurrentUserData() {
        UserDetailsDto currentUser = userContext.getCurrentUser();
        log.info("Fetching data for current user: {}", currentUser.getEmail());
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponseDto<>("Current User Fetched Successfully", HttpStatus.OK.value(), currentUser));
    }

    // Handle user signup
    @Override
    public ResponseEntity<ApiResponseDto<?>> signUpUser(CreateUserRequestDto createUserRequestDto) throws Exception {
        log.info("Attempting to register user with email: {}", createUserRequestDto.getEmail());

        // Validate required fields are present
        if (createUserRequestDto.getPassword() == null || createUserRequestDto.getGender() == null || createUserRequestDto.getFirstName() == null||createUserRequestDto.getLastName()==null||createUserRequestDto.getEmail()==null) {
            log.error("Missing required parameters for user registration.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponseDto<>("Required Parameters Are Missing", HttpStatus.BAD_REQUEST.value(), null));
        }

        //Validate the required Fields whether they are
        // presenting in right format or not
        if (!ValidationUtilities.isValidEmail(createUserRequestDto.getEmail())) {
            log.warn("Invalid email format: {}",
                    createUserRequestDto.getEmail());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponseDto(
                    "Invalid email format.",
                    HttpStatus.BAD_REQUEST.value(), null));
        }
        if (!ValidationUtilities.areValidNames(createUserRequestDto.getFirstName(),createUserRequestDto.getLastName())) {
            log.warn("Invalid name format: {}",
                    createUserRequestDto.getFirstName(),
                    createUserRequestDto.getLastName());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponseDto(
                    "Invalid name format.",
                    HttpStatus.BAD_REQUEST.value(),
                    createUserRequestDto.getFirstName()+" "+
                    createUserRequestDto.getLastName()));
        }
        if (!ValidationUtilities.isValidPassword(createUserRequestDto.getPassword())) {
            log.warn("Invalid Password Format");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponseDto(
                    "Password should be one Uppercase , One LowerCase , One Special Character and of length 8-20 characters",
                    HttpStatus.BAD_REQUEST.value(),
                    null));
        }
        if (!ValidationUtilities.isValidGender(createUserRequestDto.getGender())) {
            log.warn("Invalid Gemder Format");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponseDto(
                    "Invalid Gender Format",
                    HttpStatus.BAD_REQUEST.value(),
                    null));
        }




        // Check if the user already exists
        User existingUser = userRepository.findByEmail(createUserRequestDto.getEmail());
        if (existingUser != null) {
            log.warn("Email {} is already registered.", createUserRequestDto.getEmail());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponseDto<>("This email is already associated with another account", HttpStatus.BAD_REQUEST.value(), null));
        }




        // Create new user
        User user = new User(createUserRequestDto);
        Role userRole = roleRepository.findByName("USER").orElseThrow(() -> new Exception("Role not found"));
        user.getRoles().add(userRole);

        // Encrypt the password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Save the user
        userRepository.save(user);
        log.info("User registered successfully with email: {}", createUserRequestDto.getEmail());

        // Return response
        UserResponseDto userResponseDto = new UserResponseDto(user);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponseDto<>("Registration Successful", HttpStatus.OK.value(), userResponseDto));
    }

    // Handle user login
    @Override
    public ResponseEntity<ApiResponseDto<?>> LoginUser(LoginRequestDto loginRequest) throws Exception {
        log.info("Attempting to login with email: {}", loginRequest.getEmail());

        if (loginRequest.getPassword() == null ||loginRequest.getEmail()==null) {
            log.error("Missing required parameters for " +
                    "Login" );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponseDto<>("Required Parameters Are Missing", HttpStatus.BAD_REQUEST.value(), null));
        }
        if (!ValidationUtilities.isValidEmail(loginRequest.getEmail())) {
            log.warn("Invalid email format: {}",
                    loginRequest.getEmail());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponseDto(
                    "Invalid email format.",
                    HttpStatus.BAD_REQUEST.value(), null));
        }

        Authentication authentication = authenticate(loginRequest.getEmail(), loginRequest.getPassword());
        String email = authentication.getName();

        User user = userRepository.findByEmail(email);
        if (user == null) {
            log.error("User not found with email: {}", email);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponseDto<>("User Not Found With Related email", HttpStatus.BAD_REQUEST.value(), null));
        }

        // Check if user is blocked
        if (user.getBlocked()) {
            log.warn("Blocked user attempted to log in: {}", email);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ApiResponseDto<>("Blocked " +
                            "User", HttpStatus.OK.value()
                            ,
                            "User is blocked by: " + user.getBlockedUnblockedBy() + " For Reason " + user.getBlockedUnblockedReason()));
        }

        // Generate JWT token
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (Role role : user.getRoles()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName())); // Adding "ROLE_" prefix
        }
        String token = JwtProvider.generateToken(email, authorities);
        user.setUserToken(token);
        userService.saveToken(user.getId(), token);

        log.info("User logged in successfully: {}", email);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponseDto<>("User Logged in Successfully", HttpStatus.OK.value(), token));
    }


    // Handle OAuth login
    @Override
    public ResponseEntity<ApiResponseDto<?>> LoginByOauth(org.springframework.security.core.Authentication authentication) throws Exception {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");

        // Check if the user exists in the database
        User user = userRepository.findByEmail(email);
        if (user == null) {
            // If user doesn't exist, create a new one
            log.info("New OAuth user detected, creating account for email: {}", email);
            user = new User();
            user.setEmail(email);
            user.setFirstName(oAuth2User.getAttribute("firstName"));
            user.setLastName(oAuth2User.getAttribute("lastName"));
            user.setBlocked(false);
            userService.registeruser(user);
        }

        // Generate JWT token
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (Role role : user.getRoles()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));
        }
        String token = JwtProvider.generateToken(email, authorities);
        user.setUserToken(token);
        userService.saveToken(user.getId(), token);

        log.info("OAuth user logged in successfully: {}", email);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponseDto<>("User Logged in Successfully", HttpStatus.OK.value(), token));
    }

    // Helper method to authenticate user
    private Authentication authenticate(String email, String password) {
        log.info("Authenticating user with email: {}", email);
        UserDetails userDetails = customUserDetailService.loadUserByUsername(email);

        if (userDetails == null || !passwordEncoder.matches(password, userDetails.getPassword())) {
            log.error("Invalid credentials for email: {}", email);
            throw new BadCredentialsException("Invalid email or password.");
        }

        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}
