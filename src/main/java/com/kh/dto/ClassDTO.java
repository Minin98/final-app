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
public class ClassDTO {
    private int classNumber;
    private String title;
    private String description;
    private String category;
    private Timestamp createTime;
    private Timestamp updateTime;
    private int rate;
    private String thumbnail;
    private String uno;
    private String name;
}
