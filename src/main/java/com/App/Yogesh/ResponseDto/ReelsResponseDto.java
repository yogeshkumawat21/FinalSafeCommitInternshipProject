package com.App.Yogesh.ResponseDto;

import com.App.Yogesh.Models.Reels;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReelsResponseDto {
    private Integer id;
    private String title;
    private Integer userId;
    private String video;

    public ReelsResponseDto(Reels reel) {
        this.id = reel.getId();
        this.title = reel.getTitle();
        this.userId = reel.getUser().getId();
        this.video = reel.getVideo();
    }
}
