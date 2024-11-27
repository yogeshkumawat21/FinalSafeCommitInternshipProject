package com.App.Yogesh.ServiceImplmentation;

import com.App.Yogesh.Models.User;
import com.App.Yogesh.Repository.UserRepository;
import com.App.Yogesh.RequestDto.BioRequestDto;
import com.App.Yogesh.ResponseDto.ApiResponseDto;
import com.App.Yogesh.Services.BioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
@Service
public class BioServiceImplementation implements BioService {

    @Autowired
    UserRepository userRepository;

    @Value("${spring.bio.Resquesturl}")
    private String requestUrl;

    @Value("${spring.bio.ResponseUrl}")
    private String responseUrl;

    // Create or update user's bio by calling a third-party API
    @Override
    public ResponseEntity<ApiResponseDto<?>> createOrUpdateBio(int userId, BioRequestDto bioRequestDto) {
        log.info("Attempting to create or update bio for user with ID: {}", userId);

        // Check if the user exists
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            log.warn("User with ID: {} not found.", userId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ApiResponseDto<>("User Not Found With this Id", HttpStatus.NOT_FOUND.value(), null)
            );
        }

        // Interact with the third-party API to update the bio
        ThirdPartyApi thirdPartyApi = new ThirdPartyApi();
        try {
            log.info("Sending POST request to third-party API to update bio for user: {}", userId);
            String apiResponse = thirdPartyApi.sendPostRequest(responseUrl, bioRequestDto);

            // Check for successful API response
            if (apiResponse != null && !apiResponse.isEmpty()) {
                log.info("Successfully updated bio for user: {}", userId);
                return ResponseEntity.status(HttpStatus.OK).body(new ApiResponseDto<>(
                        "Bio updated successfully", HttpStatus.OK.value(), apiResponse
                ));
            } else {
                log.error("Failed to update bio for user: {} with third-party service", userId);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponseDto<>(
                        "Failed to update bio with the third-party service", HttpStatus.BAD_REQUEST.value(), null
                ));
            }

        } catch (Exception e) {
            log.error("An error occurred while contacting the external service for user ID: {}: {}", userId, e.getMessage());
            ApiResponseDto<String> errorResponse = new ApiResponseDto<>(
                    "An error occurred while contacting the external service", HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // Placeholder method for fetching bio; currently not implemented
    @Override
    public BioRequestDto getBio(int userId) {
        log.info("Fetching bio for user with ID: {}", userId);
        // Return null for now, as method is not implemented
        return null;
    }
}
