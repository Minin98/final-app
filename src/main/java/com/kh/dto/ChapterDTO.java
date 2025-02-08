package com.kh.dto;

import java.sql.Timestamp;

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
public class ChapterDTO {
    private int chapterNumber;
    private int classNumber;
    private String chapterName;
    private Timestamp chapterCreateTime;
}
