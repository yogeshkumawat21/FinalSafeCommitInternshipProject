package com.App.Yogesh.ServiceImplmentation;

import com.App.Yogesh.ResponseDto.MailBody;
import com.App.Yogesh.ResponseDto.UserDetailsDto;
import com.App.Yogesh.Models.ForgetPassword;
import com.App.Yogesh.Models.User;
import com.App.Yogesh.Repository.ForgetPasswordRepository;
import com.App.Yogesh.Repository.UserRepository;
import com.App.Yogesh.ResponseDto.ApiResponseDto;
import com.App.Yogesh.Services.EmailService;
import com.App.Yogesh.Services.ForgetPasswordService;
import com.App.Yogesh.config.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.Random;

@Slf4j
@Service
public class ForgetPasswordServiceImplementation implements ForgetPasswordService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ForgetPasswordRepository forgetPasswordRepository;

    @Autowired
    EmailService emailService;

    @Autowired
    UserContext userContext;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Generate a 6-digit OTP for password reset
    private Integer otpGenerator() {
        Random random = new Random();
        return random.nextInt(100_000, 999_999); // Generates a 6-digit OTP
    }

    @Override
    public ResponseEntity<ApiResponseDto<?>> verifyEmail(String email) {
        User user = userRepository.findByEmail(email);

        // If user is not found, return an error
        if (user == null) {
            log.warn("Attempt to verify email for a non-existing user: {}", email);
            return ResponseEntity.ok(new ApiResponseDto<>("Email Not Found", HttpStatus.EXPECTATION_FAILED.value(), null));
        }

        // Delete any existing OTP record for this user
        forgetPasswordRepository.deleteByUserId(user.getId());

        // Generate OTP
        int otp = otpGenerator();

        // Prepare and send email with OTP
        MailBody mailBody = new MailBody(email, "OTP for Password Reset Request", "This is the OTP for your password reset: " + otp);
        emailService.sendSimpleMessage(mailBody);

        // Store OTP and expiration time in the database
        ForgetPassword fp = new ForgetPassword(otp, new Date(System.currentTimeMillis() + 5 * 60 * 1000), user);
        forgetPasswordRepository.save(fp);

        // Log the successful email sending for password reset
        log.info("OTP sent to email: {}", email);

        return ResponseEntity.ok(new ApiResponseDto<>("Email sent for Verification", HttpStatus.OK.value(), null));
    }

    @Override
    public ResponseEntity<ApiResponseDto<?>> verifyOtp(Integer otp, String email) {
        User user = userRepository.findByEmail(email);

        // If user is not found, return error
        if (user == null) {
            log.warn("Attempt to verify OTP for a non-existing user: {}", email);
            return ResponseEntity.ok(new ApiResponseDto<>("User Not Found", HttpStatus.EXPECTATION_FAILED.value(), null));
        }

        // Fetch the OTP record from the database
        Optional<ForgetPassword> optionalFp = forgetPasswordRepository.findByOtpandUser(otp, user);

        // If the OTP is invalid, return error
        if (optionalFp.isEmpty()) {
            log.warn("Invalid OTP for user: {}", email);
            return ResponseEntity.ok(new ApiResponseDto<>("Unauthorized OTP", HttpStatus.EXPECTATION_FAILED.value(), null));
        }

        ForgetPassword fp = optionalFp.get();

        // Check if the OTP has expired
        if (fp.getExpirationTime().before(Date.from(Instant.now()))) {
            forgetPasswordRepository.delete(fp); // Delete expired OTP
            log.warn("OTP expired for user: {}", email);
            return ResponseEntity.ok(new ApiResponseDto<>("OTP Expired", HttpStatus.EXPECTATION_FAILED.value(), null));
        }

        // Log successful OTP verification
        log.info("OTP verified successfully for user: {}", email);

        return ResponseEntity.ok(new ApiResponseDto<>("OTP verified successfully", HttpStatus.OK.value(), null));
    }

    @Override
    public ResponseEntity<ApiResponseDto<?>> changePassword(String newPassword, String email) {
        // Fetch the current user from the context
        UserDetailsDto currentUser = userContext.getCurrentUser();

        // Find the user by email
        User user = userRepository.findByEmail(currentUser.getEmail());
        if (user == null) {
            log.warn("User not found for password change request: {}", currentUser.getEmail());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponseDto<>("User not found", HttpStatus.BAD_REQUEST.value(), null));
        }

        // Encode the new password
        String password = passwordEncoder.encode(newPassword);

        // Update the user's password in the database
        userRepository.updatePassword(email, password);

        // Log successful password change
        log.info("Password changed successfully for user: {}", currentUser.getEmail());

        return ResponseEntity.ok(new ApiResponseDto<>("Password changed successfully", HttpStatus.OK.value(), null));
    }
}
