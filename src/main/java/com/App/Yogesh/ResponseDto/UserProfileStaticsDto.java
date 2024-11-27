package com.App.Yogesh.ResponseDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileStaticsDto {

    private String name;
    private  String email;
    private int totalPosts;
    private int totalFollowers;
    private int totalFollowing;
    private int totalComments;
    private int totalReels;
    private List<String> roles;
    private boolean isBlocked;
    private Integer blockedUnblockedBy;
    private String blockedUnblockedReason;



}
