package com.App.Yogesh.Models;
import com.App.Yogesh.RequestDto.CreateUserRequestDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String firstName;
    private String middleName;
    private String lastName;
    private String gender;
    private String email;
    private String password;
    @OneToOne(mappedBy = "user",cascade = CascadeType.ALL)
    private ForgetPassword forgetPassword;
    private List<Integer> followers = new ArrayList<>();
    private List<Integer> followings = new ArrayList<>();
    @ManyToMany(fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private List<Role> roles = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts = new ArrayList<>();

    @Column(nullable = false)
    private Boolean blocked = false;

    private Integer blockedUnblockedBy;
    private String blockedUnblockedReason;
    @Lob
    @Column(length = 300)
    private String userToken;

    public User(CreateUserRequestDto createUserRequestDto) {
        this.firstName = createUserRequestDto.getFirstName();
        this.middleName = createUserRequestDto.getMiddleName();
        this.lastName = createUserRequestDto.getLastName();
        this.email = createUserRequestDto.getEmail();
        this.password = createUserRequestDto.getPassword();
        this.gender = createUserRequestDto.getGender();
        this.blocked = false;
        this.userToken = null;
    }
}