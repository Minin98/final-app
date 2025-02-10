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
public class UsersProgressDTO {
    private int usersProgressNumber;
    private String uno;
    private int classNumber;
    private int completionStatus;
    private double completionRate;
    private Timestamp progressUpdateTime;
}
