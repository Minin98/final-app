package com.kh.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class KakaoDTO {
    private String uno;
    private String kakaoId;
    private String name;
    private String nickname;
    private String email;
    private String phone;
    private int grade;
    
    public KakaoDTO(String uno, String nickname, int grade) {
        this.uno = uno;
        this.nickname = nickname;
        this.grade = grade;
    }
}
