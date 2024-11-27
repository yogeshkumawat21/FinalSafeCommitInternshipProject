package com.App.Yogesh.ServiceImplmentation;

import com.App.Yogesh.ResponseDto.UserDetailsDto;
import com.App.Yogesh.Models.Post;
import com.App.Yogesh.Models.User;
import com.App.Yogesh.Repository.PostRepository;
import com.App.Yogesh.Repository.UserRepository;
import com.App.Yogesh.RequestDto.PostRequestDto;
import com.App.Yogesh.ResponseDto.ApiResponseDto;
import com.App.Yogesh.ResponseDto.PostResponseDto;
import com.App.Yogesh.Services.PostService;
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
public class PostServiceImplementation implements PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    UserContext userContext;

    @Override
    public ResponseEntity<ApiResponseDto<?>> createNewPost(PostRequestDto post) {
        UserDetailsDto currentUser = userContext.getCurrentUser();
        User reqUser = userRepository.findByEmail(currentUser.getEmail());

        if (StringUtils.isBlank(post.getImage()) && StringUtils.isBlank(post.getVideo())) {
            log.warn("Attempt to create post without " +
                    "image and video " +
                    " by user {}", currentUser.getEmail());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponseDto<>(
                    "Attempt to create post without " +
                            "image and video by user " + currentUser.getEmail(), HttpStatus.NOT_FOUND.value(), null
            ));
        }
        // Check if the caption is missing
        if (StringUtils.isBlank(post.getCaption())) {
            log.warn("Attempt to create post without caption by user {}", currentUser.getEmail());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponseDto<>(
                    "Caption is missing", HttpStatus.NOT_FOUND.value(), null
            ));
        }

        // Creating a new post object
        Post newPost = new Post();
        newPost.setCaption(post.getCaption());
        newPost.setImage(post.getImage());
        newPost.setVideo(post.getVideo());
        newPost.setCreatedAt(LocalDateTime.now()); // Set the creation time
        newPost.setUser(reqUser);

        postRepository.save(newPost);
        log.info("Post created successfully with ID {} by user {}", newPost.getId(), reqUser.getEmail());

        PostResponseDto postDto = new PostResponseDto(newPost);
        return ResponseEntity.ok(new ApiResponseDto<>(
                "Post Created Successfully", HttpStatus.CREATED.value(), postDto
        ));
    }

    @Override
    public ResponseEntity<ApiResponseDto<?>> deletePostbyId(Integer postId) {
        UserDetailsDto currentUser = userContext.getCurrentUser();
        User reqUser = userRepository.findByEmail(currentUser.getEmail());

        // Validate if the user exists
        if (reqUser == null) {
            log.error("User with email {} not found while attempting to delete post ID {}", currentUser.getEmail(), postId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponseDto<>(
                    "User not Found", HttpStatus.NOT_FOUND.value(), null
            ));
        }

        // Fetch the post to delete
        Optional<Post> opt = postRepository.findById(postId);
        Post post = opt.orElse(null);

        // Post not found handling
        if (post == null) {
            log.warn("Post ID {} not found for deletion", postId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponseDto<>(
                    "Post not found", HttpStatus.NOT_FOUND.value(), null
            ));
        } else {
            // Check if the logged-in user is the owner of the post
            if (post.getUser().getId() != reqUser.getId()) {
                log.warn("User {} tried to delete another user's post with ID {}", currentUser.getEmail(), postId);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponseDto<>(
                        "You can't delete another user's post", HttpStatus.BAD_REQUEST.value(), null
                ));
            }

            postRepository.delete(post);
            log.info("Post ID {} deleted by user {}", postId, currentUser.getEmail());
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponseDto<>(
                    "Post Deleted successfully", HttpStatus.OK.value(), null
            ));
        }
    }

    @Override
    public ResponseEntity<ApiResponseDto<?>> findPostById(Integer postId) {
        Optional<Post> optionalPost = postRepository.findById(postId);

        // Post not found
        if (optionalPost.isEmpty()) {
            log.warn("Post with ID {} not found", postId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponseDto<>(
                    "Post not found", HttpStatus.NOT_FOUND.value(), null
            ));
        }

        Post post = optionalPost.get();
        PostResponseDto postDto = new PostResponseDto(post);
        log.info("Post ID {} found successfully", postId);

        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponseDto<>(
                "Post Found Successfully", HttpStatus.OK.value(), postDto
        ));
    }

    @Override
    public ResponseEntity<ApiResponseDto<?>> findAllPostByUserId(Integer userId) {
        // Check if the user exists
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            log.warn("User not found with ID {}", userId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponseDto<>(
                    "User not found with the provided ID", HttpStatus.NOT_FOUND.value(), null
            ));
        }

        User user = userOpt.get();

        // Check if the user is blocked
        if (Boolean.TRUE.equals(user.getBlocked())) {
            log.warn("User with ID {} is blocked. Cannot retrieve posts.", userId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiResponseDto<>(
                    "User is blocked. You cannot access their posts.", HttpStatus.FORBIDDEN.value(), null
            ));
        }

        // Fetch posts for the user
        List<Post> posts = postRepository.findPostByUserId(userId);

        // Check if posts exist for the user
        if (posts.isEmpty()) {
            log.warn("No posts found for user ID {}", userId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponseDto<>(
                    "No posts found for the given user", HttpStatus.NOT_FOUND.value(), null
            ));
        }

        // Convert and return posts
        List<PostResponseDto> postDtos = posts.stream()
                .map(PostResponseDto::new)
                .collect(Collectors.toList());
        log.info("Retrieved {} posts for user ID {}", posts.size(), userId);

        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponseDto<>(
                "Posts retrieved successfully", HttpStatus.OK.value(), postDtos
        ));
    }


    @Override
    public ResponseEntity<ApiResponseDto<?>> findAllPost() {
        // Fetch all posts
        List<Post> posts = postRepository.findAll();

        // Check if any posts exist
        if (posts.isEmpty()) {
            log.warn("No posts found in the system");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponseDto<>(
                    "No Posts Found", HttpStatus.NOT_FOUND.value(), null
            ));
        }

        // Filter out posts belonging to blocked users
        List<PostResponseDto> postDtos = posts.stream()
                .filter(post -> post.getUser() != null && !Boolean.TRUE.equals(post.getUser().getBlocked()))
                .map(PostResponseDto::new)
                .collect(Collectors.toList());

        // Check if any valid posts remain after filtering
        if (postDtos.isEmpty()) {
            log.warn("All posts belong to blocked users, none are visible");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponseDto<>(
                    "No Posts Found from unblocked users", HttpStatus.NOT_FOUND.value(), null
            ));
        }

        log.info("Retrieved {} posts from the system, excluding those from blocked users", postDtos.size());
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponseDto<>(
                "Posts retrieved successfully", HttpStatus.OK.value(), postDtos
        ));
    }


    @Override
    public ResponseEntity<ApiResponseDto<?>> likePost(Integer postId) {
        UserDetailsDto currentUser = userContext.getCurrentUser();
        User reqUser = userRepository.findByEmail(currentUser.getEmail());

        if (reqUser == null) {
            log.error("User {} not found while liking post", currentUser.getEmail());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponseDto<>("User not found", HttpStatus.NOT_FOUND.value(), null));
        }

        Optional<Post> optionalPost = postRepository.findById(postId);
        if (optionalPost.isEmpty()) {
            log.warn("Post ID {} not found while attempting to like", postId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponseDto<>("Post not found", HttpStatus.NOT_FOUND.value(), null));
        }

        Post post = optionalPost.get();

        // Check if the post belongs to a blocked user
        if (post.getUser() != null && Boolean.TRUE.equals(post.getUser().getBlocked())) {
            log.warn("User {} cannot like post ID {} as it belongs to a blocked user", currentUser.getEmail(), postId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponseDto<>("Cannot like this post. The post belongs to a blocked user.",
                            HttpStatus.FORBIDDEN.value(), null));
        }

        // Toggle like status for the post
        if (post.getLiked().contains(reqUser)) {
            post.getLiked().remove(reqUser);
            log.info("User {} removed their like from post ID {}", currentUser.getEmail(), postId);
        } else {
            post.getLiked().add(reqUser);
            log.info("User {} liked post ID {}", currentUser.getEmail(), postId);
        }

        postRepository.save(post);
        PostResponseDto postDto = new PostResponseDto(post);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponseDto<>(
                "Post like status updated successfully", HttpStatus.OK.value(), postDto
        ));
    }
}
