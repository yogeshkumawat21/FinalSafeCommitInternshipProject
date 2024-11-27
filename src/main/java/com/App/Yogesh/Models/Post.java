package com.App.Yogesh.Models;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String image;
    private String Caption;
    private String video ;
    @ManyToOne(cascade = CascadeType.ALL)
    private  User user ;
    private LocalDateTime createdAt;
    @OneToMany(cascade = CascadeType.ALL)
    private List<User> liked = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL)
    private List<Comment> comments = new ArrayList<>();



}
