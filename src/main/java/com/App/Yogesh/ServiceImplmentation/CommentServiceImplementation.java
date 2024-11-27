package com.App.Yogesh.ServiceImplmentation;

import com.App.Yogesh.ResponseDto.UserDetailsDto;
import com.App.Yogesh.Models.Comment;
import com.App.Yogesh.Models.Post;
import com.App.Yogesh.Repository.CommentRepository;
import com.App.Yogesh.Repository.PostRepository;
import com.App.Yogesh.Repository.UserRepository;
import com.App.Yogesh.RequestDto.CommentRequestDto;
import com.App.Yogesh.ResponseDto.ApiResponseDto;
import com.App.Yogesh.ResponseDto.CommentResponseDto;
import com.App.Yogesh.Services.CommentService;
import com.App.Yogesh.Services.PostService;
import com.App.Yogesh.Services.UserService;
import com.App.Yogesh.Models.User;
import com.App.Yogesh.config.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
public class CommentServiceImplementation implements CommentService {

    @Autowired
    UserContext userContext;

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    UserRepository userRepository;

    @Override
    public ResponseEntity<ApiResponseDto<?>> createComment(CommentRequestDto commentRequestDto) {
        // Check if the comment content is null or empty
        if (commentRequestDto.getContent() == null || commentRequestDto.getContent().isEmpty()) {
            log.warn("Empty comment content provided.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponseDto<>(
                    "Comment content can't be empty",
                    HttpStatus.NO_CONTENT.value(),
                    null
            ));
        }

        // Check if the post ID is invalid (null or less than or equal to 0)
        if (commentRequestDto.getPostId() == null || commentRequestDto.getPostId() <= 0) {
            log.warn("Invalid Post ID provided.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponseDto<>(
                    "Invalid Post ID",
                    HttpStatus.BAD_REQUEST.value(),
                    null
            ));
        }

        // Check if the post with the provided ID exists in the database
        Optional<Post> optionalPost = postRepository.findById(commentRequestDto.getPostId());
        if (optionalPost.isEmpty()) {
            log.warn("Post with ID {} not found.", commentRequestDto.getPostId());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponseDto<>(
                    "Post not found",
                    HttpStatus.NOT_FOUND.value(),
                    null
            ));
        }

        // Check if the owner of the post is blocked
        Post post = optionalPost.get();
        User postOwner = post.getUser(); // Assuming Post entity has a 'User' field for its owner

        if (postOwner.getBlocked()) { // Assuming 'isBlocked()'
            // method or field exists in the User entity
            log.warn("Cannot comment on post. Owner of post (ID: {}) is blocked.", commentRequestDto.getPostId());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiResponseDto<>(
                    "Cannot comment on a post owned by a blocked user.",
                    HttpStatus.FORBIDDEN.value(),
                    null
            ));
        }

        // If the post exists and the owner is not blocked, create a new comment and save it
        Comment newComment = new Comment();
        newComment.setContent(commentRequestDto.getContent());
        newComment.setPost(post);  // Link the comment to the post
        newComment.setCreatedAt(LocalDateTime.now()); // Set the comment creation timestamp

        // Assuming you have a user object for the comment author (currentUser)
        User currentUser = userRepository.findByEmail(userContext.getCurrentUser().getEmail());
        newComment.setUser(currentUser); // Set the comment's author (the user creating the comment)

        // Save the comment to the database
        commentRepository.save(newComment);
        post.getComments().add(newComment); // Add the comment to the post's comment list
        postRepository.save(post); // Save the post again to update the comments list

        log.info("Comment created successfully for post ID {} by user {}", commentRequestDto.getPostId(), currentUser.getEmail());

        // Return success response
        CommentResponseDto commentDto = new CommentResponseDto(newComment);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponseDto<>(
                "Comment created successfully",
                HttpStatus.CREATED.value(),
                commentDto // Send back the created comment details
        ));
    }



    @Override
    public ResponseEntity<ApiResponseDto<?>> likeComment(Integer commentId) {
        // Check if the comment ID is valid
        if (commentId == null || commentId <= 0) {
            log.warn("Invalid Comment ID provided.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponseDto<>(
                    "Invalid Comment ID",
                    HttpStatus.BAD_REQUEST.value(),
                    null
            ));
        }

        // Check if the comment exists in the database
        Optional<Comment> optionalComment = commentRepository.findById(commentId);
        if (optionalComment.isEmpty()) {
            log.warn("Comment with ID {} not found.", commentId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponseDto<>(
                    "Comment not found",
                    HttpStatus.NOT_FOUND.value(),
                    null
            ));
        }

        // Retrieve the comment and its owner
        Comment comment = optionalComment.get();
        User commentOwner = comment.getUser(); // Assuming Comment entity has a 'User' field for its owner

        // Check if the comment owner is blocked
        if (commentOwner.getBlocked()) { // Assuming 'isBlocked()'
            // method or field exists in the User entity
            log.warn("Cannot like comment. Owner of comment (ID: {}) is blocked.", commentId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiResponseDto<>(
                    "Cannot like a comment owned by a blocked user.",
                    HttpStatus.FORBIDDEN.value(),
                    null
            ));
        }

        // Perform the like action
        User currentUser = userRepository.findByEmail(userContext.getCurrentUser().getEmail());
        if (comment.getLiked().contains(currentUser)) {
            log.warn("User {} already liked the comment ID {}.", currentUser.getEmail(), commentId);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponseDto<>(
                    "You have already liked this comment",
                    HttpStatus.CONFLICT.value(),
                    null
            ));
        }

        // Add the like
        comment.getLiked().add(currentUser); // Assuming Comment
        // entity has a Set<User> likes field
        commentRepository.save(comment);

        log.info("User {} liked comment ID {} successfully.", currentUser.getEmail(), commentId);

        // Return success response
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponseDto<>(
                "Comment liked successfully",
                HttpStatus.OK.value(),
                null
        ));
    }

}
