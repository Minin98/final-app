package com.kh.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UsersDTO {
    private String uno;
    private String id;
    private String nickname;
    private String password;
    private String name;
    private String email;
    private String phone;
    private int grade;
    private String profilepath;

	public UsersDTO(String id, String nickname, String password, String name, String email, String phone, int grade) {
		this.id = id;
		this.nickname = nickname;
		this.password = password;
		this.name = name;
		this.email = email;
		this.phone = phone;
		this.grade = grade;
	}

}