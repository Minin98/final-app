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
public class ChapterDTO {
    private int chapterNumber;
    private int classNumber;
    private String chapterName;  // ✅ 반드시 String이어야 함
    private String chapterCreateTime;
}
