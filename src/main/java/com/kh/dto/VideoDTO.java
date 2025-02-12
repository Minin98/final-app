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
    private int classNumber;
    private int chapterNumber;
    private String videoTitle;
    private String videoId;
    private int videoDuration;
    private int videoOrderNumber;
    private String videoCreateTime;

    private String classTitle;
    private String chapterTitle;

    public VideoDTO(int videoNumber, int classNumber, int chapterNumber, String videoTitle, String videoId, int videoDuration, int videoOrderNumber, String videoCreateTime) {
        this.videoNumber = videoNumber;
        this.classNumber = classNumber;
        this.chapterNumber = chapterNumber;
        this.videoTitle = videoTitle;
        this.videoId = videoId;
        this.videoDuration = videoDuration;
        this.videoOrderNumber = videoOrderNumber;
        this.videoCreateTime = videoCreateTime;
    }
}