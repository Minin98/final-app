package com.kh.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
		UsersDTO dto = new UsersDTO(name, id, nickname, password, email, phone, grade);
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
	public boolean checkId(@RequestBody Map<String, String> body) {
		String id = body.get("id");
		return usersService.checkIdExists(id);
	}

	@PostMapping("/check/nickname")
	public boolean checkNickName(@RequestBody Map<String, String> body) {
		String nickname = body.get("nickname");
		return usersService.checkNickNameExists(nickname);
	}

	@PostMapping("/MypageInfo")
	public UsersDTO userInfo(@RequestHeader("Authorization") String token) {
		UsersDTO user = usersService.findUserByUno(tokenProvider.getUserNumberFromToken(removeBearer(token)));
		// System.out.println(user);
		return user;
	}

	@PostMapping("/ProfileImage")
	public Map<String, Object> profileImage(@RequestHeader("Authorization") String token) {
    Map<String, Object> responseMap = new HashMap<>();
    UsersDTO user = usersService.findUserByUno(tokenProvider.getUserNumberFromToken(removeBearer(token)));

    String profilePath = user.getProfilepath();
		System.out.println(profilePath);
    if (profilePath != null) {
        File file = new File(profilePath);
        if (file.exists() && file.isFile()) {
            try {
                byte[] fileContent = Files.readAllBytes(file.toPath());
                String mimeType = Files.probeContentType(file.toPath());
                String encodedString = Base64.getEncoder().encodeToString(fileContent);
                responseMap.put("profileImageBase64", "data:" + mimeType + ";base64," + encodedString);
            } catch (IOException e) {
                responseMap.put("error", "이미지 변환 실패: " + e.getMessage());
            }
        } else {
            responseMap.put("error", "이미지가 존재하지 않습니다.");
        }
    } else {
        responseMap.put("error", "프로필 이미지 경로가 없습니다.");
    }

    return responseMap;
}
	
	@PostMapping("/UpdateUserProfile")
	public Map<String, Object> userupdate(@RequestHeader("Authorization") String token, @RequestPart(value = "file", required = false) MultipartFile file) {
		UsersDTO user = usersService.findUserByUno(tokenProvider.getUserNumberFromToken(removeBearer(token)));
		String userId = user.getUno();
		HashMap<String, Object> map = new HashMap<>();
			File root = new File("c:\\KHLMS\\users\\" + userId);
			if (!root.exists()) {
				root.mkdirs();
			} 
			if (file != null && !file.isEmpty()) {
				try {
					String fileName = file.getOriginalFilename();
					String filePath = root + File.separator + fileName;
						file.transferTo(new File(filePath));
						user.setProfilepath(filePath);
						usersService.updateprofilepath(user);
					} catch (IOException e) {
						e.printStackTrace();
						map.put("error1", "서버에서 오류 발생");
						return map;
					}
				}
		return map;
	}
	
	private String removeBearer(String token) {
		return token != null && token.startsWith("Bearer ")? token.substring("Bearer ".length()).trim(): null;
	}
}
