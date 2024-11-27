package com.App.Yogesh.ServiceImplmentation;

import com.App.Yogesh.ResponseDto.UserDetailsDto;
import com.App.Yogesh.Models.Reels;
import com.App.Yogesh.Models.User;
import com.App.Yogesh.Repository.ReelsRepository;
import com.App.Yogesh.Repository.UserRepository;
import com.App.Yogesh.RequestDto.ReelsRequestDto;
import com.App.Yogesh.ResponseDto.ApiResponseDto;
import com.App.Yogesh.ResponseDto.ReelsResponseDto;
import com.App.Yogesh.Services.ReelsService;
import com.App.Yogesh.Services.UserService;
import com.App.Yogesh.config.UserContext;
import com.nimbusds.oauth2.sdk.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j  // Lombok annotation for logging
public class ReelsServiceImplementation implements ReelsService {

    @Autowired
    private ReelsRepository reelsRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private UserContext userContext;

    @Autowired
    private UserRepository userRepository;

    @Override
    public ResponseEntity<ApiResponseDto<?>> createReels(ReelsRequestDto reel) {
        UserDetailsDto currentUser = userContext.getCurrentUser();
        User reqUser = userRepository.findByEmail(currentUser.getEmail());
        if (StringUtils.isBlank(reel.getTitle()) || StringUtils.isBlank(reel.getVideo())) {
            log.warn("User {} tried to create a reel without a video", currentUser.getEmail());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponseDto<>(
                    "Select Video & Title to Upload",
                    HttpStatus.NOT_FOUND.value(),
                    null
            ));
        }
        // Create a new Reels entity and save it to the database
        Reels createReel = new Reels();
        createReel.setTitle(reel.getTitle());
        createReel.setUser(reqUser);
        createReel.setVideo(reel.getVideo());
        reelsRepository.save(createReel);

        // Log the successful creation of the reel
        log.info("Reels created successfully with ID {} by user {}", createReel.getId(), currentUser.getEmail());

        ReelsResponseDto reelDto = new ReelsResponseDto(createReel);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponseDto<>(
                "Reels Created Successfully",
                HttpStatus.CREATED.value(),
                reelDto
        ));
    }

    @Override
    public ResponseEntity<ApiResponseDto<?>> findAllReels() {
        // Get the current authenticated user
        UserDetailsDto currentUser = userContext.getCurrentUser();
        User reqUser = userRepository.findByEmail(currentUser.getEmail());

        // Check if the current user is found
        if (reqUser == null) {
            log.warn("User {} not found in the system", currentUser.getEmail());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponseDto<>(
                    "User not found",
                    HttpStatus.NOT_FOUND.value(),
                    null
            ));
        }

        // Fetch all reels
        List<Reels> reels = reelsRepository.findAll();

        // Filter reels to include only those by the current user and that are not blocked
        List<Reels> userReels = reels.stream()
                .filter(reel -> reel.getUser().getId()==
                        (reqUser.getId()) && !reel.getUser().getBlocked())
                .collect(Collectors.toList());

        // Check if the filtered list of reels is empty
        if (userReels.isEmpty()) {
            log.warn("No reels found for user {} that are not blocked", currentUser.getEmail());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponseDto<>(
                    "No Reels Found for user that are not blocked",
                    HttpStatus.NOT_FOUND.value(),
                    null
            ));
        }

        // Map filtered Reels entities to DTOs
        List<ReelsResponseDto> reelDtos = userReels.stream()
                .map(ReelsResponseDto::new)  // Mapping to DTO
                .collect(Collectors.toList());

        // Log the number of reels fetched
        log.info("Fetched {} reels for user {} from the database", userReels.size(), currentUser.getEmail());

        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponseDto<>(
                "Reels Fetched Successfully for user",
                HttpStatus.OK.value(),
                reelDtos
        ));
    }

        public ResponseEntity<ApiResponseDto<?>> findReelByUserId(Integer userId) {
            // Fetch the user by ID from the repository
            Optional<User> userOptional = userRepository.findById(userId);

            // Check if the user exists
            if (userOptional.isEmpty()) {
                log.warn("User with ID {} not found", userId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponseDto<>(
                        "User not found",
                        HttpStatus.NOT_FOUND.value(),
                        null
                ));
            }

            // Get the user object
            User user = userOptional.get();

            // Check if the user is blocked
            if (user.getBlocked()) {
                log.warn("User with ID {} is blocked", userId);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiResponseDto<>(
                        "User is blocked",
                        HttpStatus.FORBIDDEN.value(),
                        null
                ));
            }

            // Fetch all reels associated with the user
            List<Reels> reels = reelsRepository.findByUserId(userId);

            // Check if any reels are found for the given user
            if (reels.isEmpty()) {
                log.warn("No reels found for user ID {}", userId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponseDto<>(
                        "No Reels Found",
                        HttpStatus.NOT_FOUND.value(),
                        null
                ));
            }

            // Map Reels entities to DTOs
            List<ReelsResponseDto> reelDtos = reels.stream()
                    .map(ReelsResponseDto::new)  // Mapping to DTO
                    .collect(Collectors.toList());

            // Log the number of reels found for the user
            log.info("Fetched {} reels for user ID {}", reels.size(), userId);

            return ResponseEntity.status(HttpStatus.FOUND).body(new ApiResponseDto<>(
                    "Reels Fetched Successfully",
                    HttpStatus.FOUND.value(),
                    reelDtos
            ));
        }

    }
