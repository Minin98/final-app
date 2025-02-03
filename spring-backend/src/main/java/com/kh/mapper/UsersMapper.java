package com.kh.mapper;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.kh.dto.KakaoDTO;
import com.kh.dto.UsersDTO;

@Mapper
public interface UsersMapper {

	UsersDTO findUserByIdAndPassword(Map<String, String> map);

	int insertUser(UsersDTO dto);

	int checkIdExists(String id);

	int checkNickNameExists(String nickname);

	int checkKakaoId(String uno);

	int insertKakaoUser(Map<String, Object> map);

	KakaoDTO findKakaoUser(String uno);


}
