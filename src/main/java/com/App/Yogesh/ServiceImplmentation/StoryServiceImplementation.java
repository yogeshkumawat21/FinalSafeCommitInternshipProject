package com.App.Yogesh.ServiceImplmentation;

import com.App.Yogesh.ResponseDto.UserDetailsDto;
import com.App.Yogesh.Models.Story;
import com.App.Yogesh.Models.User;
import com.App.Yogesh.Repository.StoryRepository;
import com.App.Yogesh.Repository.UserRepository;
import com.App.Yogesh.RequestDto.StoryRequestDto;
import com.App.Yogesh.ResponseDto.ApiResponseDto;
import com.App.Yogesh.ResponseDto.StoryResponseDto;
import com.App.Yogesh.Services.StoryService;
import com.App.Yogesh.Services.UserService;
import com.App.Yogesh.config.UserContext;
import com.nimbusds.oauth2.sdk.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j  // Lombok annotation for logging
public class StoryServiceImplementation implements StoryService {

    @Autowired
    private UserService userService;

    @Autowired
    private StoryRepository storyRepository;

    @Autowired
    private UserContext userContext;

    @Autowired
    private UserRepository userRepository;

    @Override
    public ResponseEntity<ApiResponseDto<?>> createStroy(StoryRequestDto story) {
        // Fetch the current user using the context
        UserDetailsDto currentUser = userContext.getCurrentUser();
        if (StringUtils.isBlank(story.getCaptions()) || StringUtils.isBlank(story.getImage())) {
            log.warn("User {} attempted to create a story without required fields (captions or image)", currentUser.getEmail());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponseDto<>(
                    "Fill Required To Create Story",
                    HttpStatus.NOT_ACCEPTABLE.value(),
                    null
            ));
        }

        User reqUser = userRepository.findByEmail(currentUser.getEmail());


        // Create a new Story entity
        Story createdStory = new Story();
        createdStory.setCaptions(story.getCaptions());
        createdStory.setImage(story.getImage());
        createdStory.setUser(reqUser);
        createdStory.setTimeStamp(LocalDateTime.now());

        // Save the created story to the database
        storyRepository.save(createdStory);

        // Log the story creation process
        log.info("Story created successfully with ID {} by user {}", createdStory.getId(), currentUser.getEmail());

        // Convert Story entity to StoryResponseDto for the response
        StoryResponseDto storyDto = new StoryResponseDto(createdStory);

        // Return success response with the created story DTO
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponseDto<>(
                "Story created Successfully",
                HttpStatus.CREATED.value(),
                storyDto
        ));
    }

    @Override
    public ResponseEntity<ApiResponseDto<?>> findStoryByUserId(Integer userId) throws Exception {
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

        // Fetch stories by userId
        List<Story> stories = storyRepository.findByUserId(userId);

        // Check if no stories are found for the given user
        if (stories.isEmpty()) {
            log.warn("No stories found for user with ID {}", userId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponseDto<>(
                    "No Story Found",
                    HttpStatus.NOT_FOUND.value(),
                    null
            ));
        }

        // Convert Story entities to DTOs for response
        List<StoryResponseDto> storyDtos = stories.stream()
                .map(StoryResponseDto::new) // Using the constructor to convert Story to DTO
                .collect(Collectors.toList());

        // Log the successful retrieval of stories
        log.info("Fetched {} stories for user ID {}", stories.size(), userId);

        // Return the list of stories in the response
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponseDto<>(
                "Story Fetched Successfully",
                HttpStatus.OK.value(),
                storyDtos
        ));
    }

}
