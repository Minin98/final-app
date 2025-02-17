package com.kh.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class NoticeDTO {
    private int noticeNumber;
    private String noticeTitle;
    private String noticeContent;
    private String noticeCreateTime;
    private String uno;
    private int classNumber;
}