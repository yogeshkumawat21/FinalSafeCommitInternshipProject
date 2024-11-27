package com.App.Yogesh.ResponseDto;

import com.App.Yogesh.Models.Comment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponseDto {
    private Integer id;
    private String content;
    private Integer userId;
    private List<Integer> likedUserIds;
    private LocalDateTime createdAt;
    private Integer postId;

    public CommentResponseDto(Comment comment) {
        this.id = comment.getId();
        this.content = comment.getContent();
        this.userId = comment.getUser().getId();
        this.likedUserIds = comment.getLiked().stream()
                .map(user -> user.getId())
                .collect(Collectors.toList());
        this.createdAt = comment.getCreatedAt();
        this.postId = comment.getPost() != null ? comment.getPost().getId() : null;
    }
}
