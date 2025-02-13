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
public class NoticeDTO {
    private int noticeNumber;
    private String noticeTitle;
    private String noticeContent;
    private String noticeCreateTime;
    private String uno;
    private int classNumber;
}