package com.kh.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.kh.dto.KakaoDTO;
import com.kh.dto.UsersDTO;
import com.kh.mapper.UsersMapper;

@Service
public class UsersService {
	private UsersMapper mapper;

	public UsersService(UsersMapper mapper) {
		this.mapper = mapper;
	}

	public UsersDTO login(String id, String passwd) {
		try {
			Map<String, String> map = new HashMap<String, String>();
			map.put("id", id);
			map.put("password", passwd);
			return mapper.findUserByIdAndPassword(map);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public int insertUser(UsersDTO dto) {
		return mapper.insertUser(dto);
	}

	public boolean checkIdExists(String id) {
		int count = mapper.checkIdExists(id);
		return count > 0;
	}

	public boolean checkNickNameExists(String nickname) {
		int count = mapper.checkNickNameExists(nickname);
		return count > 0;
	}

	public KakaoDTO kakaoLogin(String uno, String nickname, int grade) {
		Map<String, Object> map = new HashMap<>();
		int count = mapper.checkKakaoId(uno);

		map.put("uno", uno);
		map.put("nickname", nickname);
		map.put("grade", grade);

		if (count == 0) {
			// 유저가 없으면 회원가입
			int result = mapper.insertKakaoUser(map);
		}

		// 유저 정보를 다시 불러와서 반환
		return mapper.findKakaoUser(uno);
	}
}
