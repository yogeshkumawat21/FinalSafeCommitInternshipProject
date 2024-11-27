package com.App.Yogesh.ResponseDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AllUserStaticsResponseto {
    private Long totalUsers;
    private Long activeUsers;
    private Long blockedUsers;
    private double averagePostsPerUser;
    private Long totalPosts;



}
