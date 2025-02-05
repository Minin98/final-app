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

	int checkUsersNickname(String nickname);

	int checkKakaoNickname(String nickname);

	UsersDTO findUserByUno(String string);

	UsersDTO updateUser(UsersDTO user);

	int updateprofilepath(UsersDTO user);

}
