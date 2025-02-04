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
		System.out.println(id);
		int count = mapper.checkIdExists(id);
		System.out.println(count);
		return count > 0;
	}

	public boolean checkNickNameExists(String nickname) {
		if(mapper.checkUsersNickname(nickname) > 0 || mapper.checkKakaoNickname(nickname) > 0)
			return true;
		return false;
	}


	
}
