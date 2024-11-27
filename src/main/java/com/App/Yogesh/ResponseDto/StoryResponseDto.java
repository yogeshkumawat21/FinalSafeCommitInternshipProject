package com.App.Yogesh.ResponseDto;

import com.App.Yogesh.Models.Story;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class StoryResponseDto {
    private Integer id;
    private Integer userId;
    private String captions;
    private String image;
    private LocalDateTime timeStamp;

    public StoryResponseDto(Story story) {
        this.id = story.getId();
        this.userId = story.getUser().getId();
        this.captions = story.getCaptions();
        this.image = story.getImage();
        this.timeStamp = story.getTimeStamp();
    }
}
