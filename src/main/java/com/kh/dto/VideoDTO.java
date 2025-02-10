package com.kh.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class VideoDTO {
    private int videoNumber;
    private int chapterNumber;
    private String videoTitle;
    private String videoId;
    private int videoDuration;
    private int videoOrderNumber;
    private String videoCreateTime;
}
