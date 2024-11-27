package com.App.Yogesh.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String content;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "comment_likes", // Custom table name
            joinColumns = @JoinColumn(name = "comment_id"), // Column name in the join table that references the comment
            inverseJoinColumns = @JoinColumn(name = "user_id") // Column name in the join table that references the user
    )
    private List<User> liked = new ArrayList<>();
    private LocalDateTime createdAt;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "post_id")
    private Post post;

}
