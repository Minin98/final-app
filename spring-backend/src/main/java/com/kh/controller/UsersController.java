package com.kh.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.kh.dto.UsersDTO;
import com.kh.service.UsersService;
import com.kh.token.JwtTokenProvider;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class UsersController {
	private final UsersService usersService;
	private final JwtTokenProvider tokenProvider;

	public UsersController(UsersService usersService, JwtTokenProvider tokenProvider) {
		super();
		this.usersService = usersService;
		this.tokenProvider = tokenProvider;
	}

	@PostMapping("/users/login")
	public Map<String, Object> login(@RequestBody Map<String, String> map) {
		Map<String, Object> result = new HashMap<>();
		String id = map.get("id");
		String passwd = map.get("passwd");
		UsersDTO member = usersService.login(id, passwd);
		boolean flag = false;
		if (member != null) {
			String token = tokenProvider.generateJwtToken(member);
			flag = true;
			result.put("token", token);
			result.put("flag", flag);
		}
		result.put("flag", flag);

		return result;
	}

	@ResponseBody
	@PostMapping("/register")
	public Map<String, Object> register(@RequestBody Map<String, String> body) {
		Map<String, Object> map = new HashMap<>();
		String id = body.get("id");
		String password = body.get("password");
		String name = body.get("username");
		String nickname = body.get("nickname");
		String email = body.get("email");
		String phone = body.get("phone");
		int grade = "student".equals(body.get("role")) ? 2 : 1;
		UsersDTO dto = new UsersDTO(name, id, nickname, password, nickname, email, phone, grade);
		int count = usersService.insertUser(dto);
		map.put("count", count);
		if (count > 0) {
			map.put("msg", "회원가입 완료");
		} else {
			map.put("msg", "회원 가입 실패, 입력하신 데이터를 확인해주세요.");
		}
		return map;
	}

	@PostMapping("/check/id")
	public boolean checkId(@RequestBody String id) {
		return usersService.checkIdExists(id);
	}

	@PostMapping("/check/nickname")
	public boolean checkNickName(@RequestBody String nickname) {
		return usersService.checkNickNameExists(nickname);
	}
}
