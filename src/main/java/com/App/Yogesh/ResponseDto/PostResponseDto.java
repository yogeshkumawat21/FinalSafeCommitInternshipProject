package com.App.Yogesh.ResponseDto;

import com.App.Yogesh.Models.Post;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostResponseDto {
    private Integer id;
    private String image;
    private String caption;
    private String video;
    private List<UserResponseDto> liked;
    private List<CommentResponseDto> comments;
     private LocalDateTime createdAt;
    private Integer userId;
    public PostResponseDto(Post post) {
        this.id = post.getId();
        this.image = post.getImage();
        this.caption = post.getCaption();
        this.video = post.getVideo();
        this.liked =
                post.getLiked().stream().map(UserResponseDto::new).collect(Collectors.toList());
        this.comments =
                post.getComments().stream().map(CommentResponseDto::new).collect(Collectors.toList()); // Convert to CommentDto
        this.createdAt = post.getCreatedAt();
        this.userId = post.getUser() != null ? post.getUser().getId() : null; // Get user ID safely
    }
}
