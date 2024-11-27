package com.App.Yogesh.Controller;

import com.App.Yogesh.RequestDto.BlockUserRequestDto;
import com.App.Yogesh.ResponseDto.ApiResponseDto;
import com.App.Yogesh.Services.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "AdminController")
@RequiredArgsConstructor
@Slf4j // Lombok for logging
public class AdminController {

  private final AdminService adminService;

  // API for Fetching All Users
  @Operation(summary = "Get All Users")
  @GetMapping("/getAllUser")
  public ResponseEntity<ApiResponseDto<?>> getAllUsers() {
    log.info("Fetching all users...");
    return adminService.getAllUser();
  }

  // API for Blocking a User
  @Operation(summary = "Block User")
  @PutMapping("/blockUser")
  public ResponseEntity<ApiResponseDto<?>> blockUser(@RequestBody BlockUserRequestDto blockUserRequestDto) {
    log.info("Blocking user with details: {}", blockUserRequestDto);
    return adminService.blockUser(blockUserRequestDto);
  }

  // API for Unblocking a User
  @Operation(summary = "Unblock User")
  @PutMapping("/unBlockUser")
  public ResponseEntity<ApiResponseDto<?>> unBlockUser(@RequestBody BlockUserRequestDto blockUserRequestDto) {
    log.info("Unblocking user with details: {}", blockUserRequestDto);
    return adminService.unblockUser(blockUserRequestDto);
  }


  // API for Deleting a Post
  @Operation(summary = "Delete Post")
  @DeleteMapping("/deletePost/{postId}")
  public ResponseEntity<ApiResponseDto<?>> deletePost(@PathVariable Integer postId) {
    log.info("Deleting post with ID: {}", postId);
    return adminService.deletePost(postId);
  }

  // API for Deleting a Comment
  @Operation(summary = "Delete Comment")
  @DeleteMapping("/deleteComment/{commentID}")
  public ResponseEntity<ApiResponseDto<?>> deleteComment(@PathVariable Integer commentID) {
    log.info("Deleting comment with ID: {}", commentID);
    return adminService.deleteComment(commentID);
  }

  // API for User Statistics by ID
  @Operation(summary = "User Statistics By ID")
  @GetMapping("/userStaticsByID/{userId}")
  public ResponseEntity<ApiResponseDto<?>> userProfile(@PathVariable Integer userId) throws Exception {
    log.info("Fetching user statistics for ID: {}", userId);
    return adminService.getUserProfile(userId);
  }

  // API for All User Statistics
  @Operation(summary = "Admin Statistics")
  @GetMapping("/AdminStatics")
  public ResponseEntity<ApiResponseDto<?>> adminStatics() {
    log.info("Fetching admin statistics...");
    return adminService.getAdminStatics();
  }

  // API for Adding Admin Role
  @Operation(summary = "Add Admin Role")
  @PostMapping("/addAdmin/{userId}")
  public ResponseEntity<ApiResponseDto<?>> addAdmin(@PathVariable int userId) {
    log.info("Adding admin role to user with ID: {}", userId);
    return adminService.addAdmin(userId);
  }

  // API for Removing Admin Access
  @Operation(summary = "Remove Admin Access")
  @PostMapping("/removeAdmin/{userId}")
  public ResponseEntity<ApiResponseDto<?>> removeAdmin(@PathVariable int userId) {
    log.info("Removing admin access from user with ID: {}", userId);
    return adminService.removeAdmin(userId);
  }
}
