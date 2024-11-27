package com.App.Yogesh.Repository;

import com.App.Yogesh.Models.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {

    int countByUserId(Integer userId);

    List<Comment> findByUserId(Integer userId);
    List<Comment> findByPostId(Integer postId);
    void deleteAllByPostId(Integer postId);
    @Query("SELECT c FROM Comment c WHERE c.user.id = :userId")
    List<Comment> findCommentsByUserId(@Param("userId") Integer userId);
}
