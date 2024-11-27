package com.App.Yogesh.ServiceImplmentation;
import com.App.Yogesh.ResponseDto.MailBody;
import com.App.Yogesh.ResponseDto.UserDetailsDto;
import com.App.Yogesh.ResponseDto.AllUserStaticsResponseto;
import com.App.Yogesh.Models.*;
import com.App.Yogesh.Repository.*;
import com.App.Yogesh.RequestDto.BlockUserRequestDto;
import com.App.Yogesh.ResponseDto.ApiResponseDto;
import com.App.Yogesh.ResponseDto.UserProfileStaticsDto;
import com.App.Yogesh.ResponseDto.UserResponseDto;
import com.App.Yogesh.Services.AdminService;
import com.App.Yogesh.Services.EmailService;
import com.App.Yogesh.config.UserContext;
import com.nimbusds.oauth2.sdk.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AdminServiceImplementation implements AdminService {

    @Autowired RoleRepository roleRepository;@Autowired UserRepository userRepository;@Autowired UserContext userContext;@Autowired BlackListedTokenRepository blackListedTokenRepository;@Autowired EmailService emailService;@Autowired PostRepository postRepository;@Autowired CommentRepository commentRepository;@Autowired ReelsRepository reelsRepository;

     @Override
     public ResponseEntity<ApiResponseDto<?>> getAllUser() {
         log.info("Fetching all users from the database");
         List<User> users = userRepository.findAll(); // Get all users
         if (users.isEmpty()) {
             log.warn("No users found in the database");
             return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponseDto<>(
                     "No User Found",
                     HttpStatus.NOT_FOUND.value(),
                     null
             ));
         }
         log.info("Mapping users to UserResponseDto");
         List<UserResponseDto> userDtos = users.stream()
                 .map(UserResponseDto::new)
                 .collect(Collectors.toList());

         log.info("Successfully retrieved {} users", userDtos.size());
         return ResponseEntity.ok(new ApiResponseDto<>(
                 "Users retrieved successfully",
                 HttpStatus.OK.value(),
                 userDtos
         ));
     }
    @Override
    public ResponseEntity<ApiResponseDto<?>> blockUser(BlockUserRequestDto blockUserRequestDto) {
        UserDetailsDto currentUser = userContext.getCurrentUser();
        log.info("Request received to block user with ID: {}", blockUserRequestDto.getUserId());
        if (StringUtils.isBlank(blockUserRequestDto.getReason())) {
            log.warn("User {} attempted to block  a user" +
                    " without required fields (reason or " +
                    "userId)", currentUser.getEmail());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponseDto<>(
                    "Fill Required To Block User",
                    HttpStatus.BAD_REQUEST.value(),
                    null
            ));
        }

        Optional<User> targetUser = userRepository.findById(blockUserRequestDto.getUserId());
        if (targetUser.isEmpty()) {
            log.warn("User with ID {} not found.", blockUserRequestDto.getUserId());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponseDto<>(
                    "Invalid User",
                    HttpStatus.NOT_FOUND.value(),
                    null
            ));
        }
        if (targetUser.get().getBlocked()) {
            log.warn("User with ID {} Blocked Already.",
                    blockUserRequestDto.getUserId());
            User user = targetUser.get();
            user.setBlockedUnblockedBy(userRepository.findByEmail(currentUser.getEmail()).getId());
            user.setBlockedUnblockedReason(blockUserRequestDto.getReason());
            userRepository.save(user);
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponseDto<>(
                    "User Blocked Already",
                    HttpStatus.OK.value(),
                    null
            ));
        }


        String reason = blockUserRequestDto.getReason();

        log.info("Blocking user with ID: {} by Admin: {} for reason: {}",
                targetUser.get().getId(), currentUser.getEmail(), reason);

        // Update user block status and reason
        User user = targetUser.get();
        user.setBlocked(true);
        user.setBlockedUnblockedBy(userRepository.findByEmail(currentUser.getEmail()).getId());
        user.setBlockedUnblockedReason(reason);
        userRepository.save(user);

        log.info("User with ID: {} successfully blocked.", user.getId());

        // Blacklist the user's token if available
        String userToken = user.getUserToken();
        if (userToken != null) {
            BlackListedToken blacklistedToken = new BlackListedToken();
            blacklistedToken.setUserToken(userToken);
            blacklistedToken.setUserId(user.getId());
            blackListedTokenRepository.save(blacklistedToken);
            log.info("Token for user ID: {} added to blacklist.", user.getId());
        }

        // Notify user via email
        String email = user.getEmail();
        MailBody mailBody = new MailBody(
                email,
                "User Account Blocked Notification",
                "Hello " + user.getFirstName() + ",\n\n" +
                        "We regret to inform you that your account has been blocked by Admin " + currentUser.getFirstName() +
                        " due to: " + reason + "."
        );
        emailService.sendSimpleMessage(mailBody);
        log.info("Notification email sent to user: {}", email);

        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponseDto<>(
                "Blocked User Successfully",
                HttpStatus.OK.value(),
                null
        ));
    }


    @Override
    public ResponseEntity<ApiResponseDto<?>> unblockUser(BlockUserRequestDto blockUserRequestDto) {
        UserDetailsDto currentUser = userContext.getCurrentUser();
        log.info("Request received to unblock user with ID: {}", blockUserRequestDto.getUserId());

        // Check if the reason for unblocking is provided
        if (StringUtils.isBlank(blockUserRequestDto.getReason())) {
            log.warn("User {} attempted to unblock a user without required fields (reason or userId)",
                    currentUser.getEmail());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponseDto<>(
                    "Fill Required To Unblock User",
                    HttpStatus.NOT_ACCEPTABLE.value(),
                    null
            ));
        }

        // Retrieve the target user from the repository
        Optional<User> targetUser = userRepository.findById(blockUserRequestDto.getUserId());
        if (targetUser.isEmpty()) {
            log.warn("User with ID {} not found.", blockUserRequestDto.getUserId());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponseDto<>(
                    "Invalid User",
                    HttpStatus.NOT_FOUND.value(),
                    null
            ));
        }

        User user = targetUser.get();

        // Check if the user is already not blocked
        if (!user.getBlocked()) {
            log.warn("User with ID {} is not blocked, no action taken.", user.getId());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponseDto<>(
                    "User is not blocked, no need to unblock.",
                    HttpStatus.BAD_REQUEST.value(),
                    null
            ));
        }

        String reason = blockUserRequestDto.getReason();
        log.info("Unblocking user with ID: {} by Admin: {} for reason: {}",
                user.getId(), currentUser.getEmail(), reason);

        // Update the target user's block status to 'false'
        user.setBlocked(false);
        user.setBlockedUnblockedBy(userRepository.findByEmail(currentUser.getEmail()).getId()); // Set the Admin who unblocked
        user.setBlockedUnblockedReason(null); // Clear any previous block reason
        user.setBlockedUnblockedReason(reason); // Set the unblock reason
        userRepository.save(user);

        log.info("User with ID: {} successfully unblocked.", user.getId());

        // If the user has a token, remove it from the blacklist
        String userToken = user.getUserToken();
        if (userToken != null) {
            blackListedTokenRepository.deleteByUserToken(userToken);
            log.info("Token for user ID: {} removed from blacklist.", user.getId());
        }

        // Send email notification to the user
        String email = user.getEmail();
        MailBody mailBody = new MailBody(
                email,
                "User Account Unblocked Notification",
                "Hello " + user.getFirstName() + ",\n\n" +
                        "We are pleased to inform you that your account has been unblocked by Admin " + currentUser.getFirstName() +
                        " due to: " + reason + "."
        );
        emailService.sendSimpleMessage(mailBody);
        log.info("Notification email sent to user: {}", email);

        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponseDto<>(
                "Unblocked User Successfully",
                HttpStatus.OK.value(),
                null
        ));
    }





    @Override
    public ResponseEntity<ApiResponseDto<?>> deletePost(Integer postId) {
        log.info("Request received to delete post with ID: {}", postId);

        // Retrieve the post from the repository by ID
        Optional<Post> targetPost = postRepository.findById(postId);
        if (targetPost.isEmpty()) {
            log.warn("Post with ID {} not found.", postId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponseDto<>(
                    "Post Not Found",
                    HttpStatus.NOT_FOUND.value(),
                    null
            ));
        }

        User targetUser = targetPost.get().getUser();
        UserDetailsDto currentUser = userContext.getCurrentUser();

        List<Comment> comments =
                commentRepository.findByPostId(postId);
        //remove users from liked comments


        postRepository.deleteById(postId);
        log.info("Post with ID: {} deleted successfully.", postId);

        // Send email notification to the user
        String email = targetUser.getEmail();
        MailBody mailBody = new MailBody(
                email,
                "Your Post Deletion Notification",
                "Hello " + targetUser.getFirstName() + ",\n\n" +
                        "We regret to inform you that your post has been deleted permanently by Admin " + currentUser.getFirstName() +
                        " due to violation of community guidelines."
        );
        emailService.sendSimpleMessage(mailBody);
        log.info("Post deletion notification email sent to user: {}", email);

        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponseDto<>(
                "Post Deleted Successfully",
                HttpStatus.OK.value(),
                null
        ));
    }

    @Override
    public ResponseEntity<ApiResponseDto<?>> deleteComment(Integer commentID) {
        log.info("Attempting to delete comment with ID: {}", commentID);

        Optional<Comment> targetComment = commentRepository.findById(commentID);

        if (targetComment.isEmpty()) {
            log.warn("Comment with ID {} not found.", commentID);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ApiResponseDto<>("Comment Not Found", HttpStatus.NO_CONTENT.value(), null)
            );
        }

        commentRepository.deleteById(commentID);
        log.info("Comment with ID {} deleted successfully.", commentID);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ApiResponseDto<>("Comment Deleted Successfully", HttpStatus.OK.value(), null)
        );
    }

    @Override
    public ResponseEntity<ApiResponseDto<?>> getUserProfile(Integer userId) {
        log.info("Fetching user profile for userId: {}",
                userId);

        // Fetch user from the repository
        Optional<User> optionalUser = userRepository.findById(userId);

        if (optionalUser.isEmpty()) {
            log.warn("No user found for userId: {}", userId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ApiResponseDto<>("No User Found for this userId", HttpStatus.NOT_FOUND.value(), null)
            );
        }

        // If user exists, gather data
        User user = optionalUser.get();
        String name = user.getFirstName() + " " + user.getLastName();
        int totalPosts = postRepository.countByUserId(userId);
        int totalFollowers = user.getFollowers().size();
        int totalFollowing = user.getFollowings().size();
        int totalComments = commentRepository.countByUserId(userId);
        int totalReels = reelsRepository.countByUserId(userId);
        List<String> roles = user.getRoles().stream().map(Role::getName).toList(); // Extract role names

        // Create DTO
        UserProfileStaticsDto userProfileStaticsDto = new UserProfileStaticsDto();
        userProfileStaticsDto.setName(name);
        userProfileStaticsDto.setEmail(user.getEmail());
        userProfileStaticsDto.setBlocked(user.getBlocked());
        userProfileStaticsDto.setBlockedUnblockedBy(user.getBlockedUnblockedBy());
        userProfileStaticsDto.setBlockedUnblockedReason(user.getBlockedUnblockedReason());
        userProfileStaticsDto.setTotalPosts(totalPosts);
            userProfileStaticsDto.setTotalFollowers(totalFollowers);
        userProfileStaticsDto.setTotalFollowing(totalFollowing);
        userProfileStaticsDto.setTotalComments(totalComments);
        userProfileStaticsDto.setTotalReels(totalReels);
        userProfileStaticsDto.setRoles(roles);

        log.info("Successfully fetched profile data for userId: {}", userId);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ApiResponseDto<>("Data fetched successfully for this UserId", HttpStatus.OK.value(), userProfileStaticsDto)
        );
    }

    @Override
    public ResponseEntity<ApiResponseDto<?>> getAdminStatics() {
        log.info("Fetching admin statistics...");

        Long totalUsers = userRepository.count();
        Long activeUsers = (long) userRepository.countByBlockedFalse();
        Long blockedUsers = (long) userRepository.countByBlockedTrue();
        Long totalPostsByUsers = postRepository.count();

        // Calculate average posts per user
        double averagePostsPerUser = totalUsers > 0 ? (double) totalPostsByUsers / totalUsers : 0.0;

        AllUserStaticsResponseto allUserStaticsResponseto = new AllUserStaticsResponseto(
                totalUsers, activeUsers, blockedUsers, averagePostsPerUser, totalPostsByUsers
        );

        log.info("Admin statistics fetched successfully: totalUsers={}, activeUsers={}, blockedUsers={}",
                totalUsers, activeUsers, blockedUsers);

        return ResponseEntity.status(HttpStatus.OK).body(
                new ApiResponseDto<>("Users Data fetched Successfully", HttpStatus.OK.value(), allUserStaticsResponseto)
        );
    }

    @Override
    public ResponseEntity<ApiResponseDto<?>> addAdmin(Integer userId) {
        log.info("Attempting to add admin role to user with ID: {}", userId);

        // Fetch the user by ID
        Optional<User> targetUser = userRepository.findById(userId);
        if (targetUser.isEmpty()) {
            log.warn("User with ID {} not found.", userId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponseDto<>("User not found", HttpStatus.NOT_FOUND.value(), null));
        }

        User user = targetUser.get();

        // Check if the user is blocked
        if (user.getBlocked()) {
            log.warn("User with ID {} is blocked.", userId);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponseDto<>("User is " +
                            "blocked. Please unblock the " +
                            "user before assigning admin " +
                            "role.", HttpStatus.BAD_REQUEST.value(), null));
        }

        // Fetch the admin role from the database
        Optional<Role> optionalAdminRole = roleRepository.findByName("ADMIN");
        if (optionalAdminRole.isEmpty()) {
            log.error("Admin role not found in the system.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponseDto<>("Admin role not found", HttpStatus.BAD_REQUEST.value(), null));
        }

        Role adminRole = optionalAdminRole.get(); // Get the Role object

        // Check if the user already has the admin role
        if (user.getRoles().contains(adminRole)) {
            log.warn("User with ID {} already has admin role.", userId);
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ApiResponseDto<>("User already has admin role", HttpStatus.CONFLICT.value(), null));
        }

        // Assign admin role to the user
        user.getRoles().add(adminRole);
        userRepository.save(user);

        log.info("Admin role added to user with ID: {}", userId);

        // Send email notification
        String email = user.getEmail();
        MailBody mailBody = new MailBody(
                email,
                "Congratulations on Your New Admin Role!",
                "Hello " + user.getFirstName() + ",\n\n" +
                        "Congratulations! We are pleased to inform you that you have been granted the Admin role.\n\n" +
                        "If you have any questions or need assistance, feel free to reach out.\n\n" +
                        "Best regards,\n" +
                        "The Social App"
        );
        emailService.sendSimpleMessage(mailBody);

        return ResponseEntity.ok(new ApiResponseDto<>("Admin role added successfully", HttpStatus.OK.value(), null));
    }

    @Override
    public ResponseEntity<ApiResponseDto<?>> removeAdmin(Integer userId) {
        log.info("Attempting to remove admin role from user with ID: {}", userId);

        Optional<User> targetUser = userRepository.findById(userId);
        if (targetUser.isEmpty()) {
            log.warn("User with ID {} not found.", userId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponseDto<>("User not found", HttpStatus.NOT_FOUND.value(), null));
        }

        Optional<Role> optionalAdminRole = roleRepository.findByName("ADMIN");
        if (optionalAdminRole.isEmpty()) {
            log.error("Admin role not found in the system.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponseDto<>("Admin role not found", HttpStatus.BAD_REQUEST.value(), null));
        }

        Role adminRole = optionalAdminRole.get(); // Get the Role object

        // Check if the user has the admin role
        if (targetUser.get().getRoles().contains(adminRole)) {
            targetUser.get().getRoles().remove(adminRole);
            userRepository.save(targetUser.get());
            String email = targetUser.get().getEmail();

            log.info("Admin role removed from user with ID: {}", userId);

            // Send email to the user
            MailBody mailBody = new MailBody(
                    email,
                    "Notice of Admin Role Removal",
                    "Hello " + targetUser.get().getFirstName() + ",\n\n" +
                            "We regret to inform you that your Admin role has been removed due to a violation of community guidelines.\n\n" +
                            "If you have any questions or would like to discuss this further, please do not hesitate to reach out.\n\n" +
                            "Best regards,\n" +
                            "The Team"
            );
            emailService.sendSimpleMessage(mailBody);

            return ResponseEntity.ok(new ApiResponseDto<>("Admin role removed successfully", HttpStatus.OK.value(), null));
        } else {
            log.warn("User with ID {} does not have admin role.", userId);
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ApiResponseDto<>("User does not have admin role", HttpStatus.CONFLICT.value(), null));
        }
    }
    }


