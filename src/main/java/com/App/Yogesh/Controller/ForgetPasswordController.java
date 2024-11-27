package com.App.Yogesh.Controller;

import com.App.Yogesh.ResponseDto.ApiResponseDto;
import com.App.Yogesh.Services.ForgetPasswordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Forget Password APIs")
@RestController
@RequestMapping("/forgetPassword")
@RequiredArgsConstructor
@Slf4j
public class ForgetPasswordController {

    private final ForgetPasswordService forgetPasswordService;

    @Operation(summary = "Get OTP by Email")
    @GetMapping("/verifyMail/{email}")
    public ResponseEntity<?> verifyEmail(@PathVariable String email) {
        log.info("Request received for email verification: {}", email);
        return forgetPasswordService.verifyEmail(email);
    }

    @Operation(summary = "Verify Email by OTP")
    @PostMapping("/verifyOtp/{otp}/{email}")
    public ResponseEntity<ApiResponseDto<?>> verifyOtp(@PathVariable Integer otp, @PathVariable String email) {
        log.info("Request to verify OTP: {} for email: {}", otp, email);
        return forgetPasswordService.verifyOtp(otp, email);
    }

    @Operation(summary = "Reset Password")
    @PostMapping("/changePassword/{newPassword}/{email}")
    public ResponseEntity<?> changePassword(@PathVariable String newPassword, @PathVariable String email) {
        log.info("Password change requested for email: {}", email);
        return forgetPasswordService.changePassword(newPassword, email);
    }
}
