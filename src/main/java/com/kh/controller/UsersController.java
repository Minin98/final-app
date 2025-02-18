package com.kh.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.kh.dto.KakaoDTO;
import com.kh.dto.UsersDTO;
import com.kh.service.KakaoService;
import com.kh.service.UsersService;
import com.kh.token.JwtTokenProvider;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class UsersController {
	private final UsersService usersService;
	private final KakaoService kakaoService;
	private final JwtTokenProvider tokenProvider;

	public UsersController(UsersService usersService, KakaoService kakaoService, JwtTokenProvider tokenProvider) {
		this.usersService = usersService;
		this.kakaoService = kakaoService;
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
	public Map<String, Object> userInfo(@RequestHeader("Authorization") String token) {
		Map<String, Object> map = new HashMap<>();
		int usertype = tokenProvider.getUserTypeFromToken(removeBearer(token));
		if (usertype == 0) {
			UsersDTO user = usersService.findUserByUno(tokenProvider.getUserNumberFromToken(removeBearer(token)));
			map.put("name", user.getName());
			map.put("phone", user.getPhone());
			map.put("email", user.getEmail());
			map.put("nickname", user.getNickname());
			map.put("grade", user.getGrade());
		} else if (usertype == 1) {
			KakaoDTO kakaoDto = kakaoService.findKakaoByUno(tokenProvider.getUserNumberFromToken(removeBearer(token)));
			map.put("name", kakaoDto.getName());
			map.put("phone", kakaoDto.getPhone());
			map.put("email", kakaoDto.getEmail());
			map.put("nickname", kakaoDto.getNickname());
			map.put("grade", kakaoDto.getGrade());
		} else {
			map.put("msg", "유효하지 않은 계정 정보입니다.");
		}
		map.put("usertype", usertype);
		return map;
	}

	@PostMapping("/ProfileImage")
	public Map<String, Object> profileImage(@RequestHeader("Authorization") String token) {
		Map<String, Object> responseMap = new HashMap<>();
		UsersDTO user = usersService.findUserByUno(
				tokenProvider.getUserNumberFromToken(removeBearer(token)));
		String profilePath = user.getProfilepath();
		System.out.println("DB에 저장된 프로필 경로: " + profilePath);
		if (!"non".equals(profilePath)) {
			File localFile = null;
			try {
				String fileName = new File(profilePath).getName();
				String localPath = "C:\\KHLMS\\users\\"
						+ user.getUno()
						+ File.separator
						+ fileName;
				localFile = new File(localPath);
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (localFile != null && localFile.exists() && localFile.isFile()) {
				responseMap.put("profileImageUrl", profilePath);
			} else {
				responseMap.put("error", "이미지가 존재하지 않습니다. 경로: " + profilePath);
			}
		} else {
			responseMap.put("error", "프로필 이미지가 설정되지 않았습니다. (\"non\")");
		}

		return responseMap;
	}

	@PostMapping("/UpdateUserProfile")
	public Map<String, Object> userupdate(
			@RequestHeader("Authorization") String token,
			@RequestPart(value = "file", required = false) MultipartFile file) {
		// 1) 토큰에서 사용자번호 추출 + DB 조회
		UsersDTO user = usersService.findUserByUno(
				tokenProvider.getUserNumberFromToken(removeBearer(token)));
		HashMap<String, Object> map = new HashMap<>();

		// 2) 물리적 디스크 저장 폴더
		File root = new File("C:\\KHLMS\\users\\" + user.getUno());
		if (!root.exists()) {
			root.mkdirs();
		}

		// 3) 업로드된 파일이 있는 경우
		if (file != null && !file.isEmpty()) {
			try {
				// 3-1) 원본 파일명
				String fileName = file.getOriginalFilename();

				// 3-2) 실제 저장될 물리 경로
				String filePath = root + File.separator + fileName;

				// (선택) 기존 프로필 파일 삭제
				deleteFilesInDirectory(root);

				// 3-3) 서버 디스크에 파일 저장
				file.transferTo(new File(filePath));

				// 3-4) DB에는 웹에서 접근 가능한 경로(/profileImages/...)를 저장
				String webPath = "/profileImages/" + user.getUno() + "/" + fileName;

				user.setProfilepath(webPath);
				usersService.updateprofilepath(user);

				map.put("profilePath", webPath);
				map.put("message", "프로필 이미지가 업데이트되었습니다.");
			} catch (IOException e) {
				e.printStackTrace();
				map.put("error1", "서버에서 오류 발생: " + e.getMessage());
				return map;
			}
		}
		// 4) 업로드된 파일이 없는 경우(프로필 제거)
		else {
			// (선택) 기존 파일 삭제
			deleteFilesInDirectory(root);

			user.setProfilepath("non");
			usersService.updateprofilepath(user);

			map.put("profilePath", "non");
			map.put("message", "프로필 이미지를 제거했습니다.");
		}

		return map;
	}

	private void deleteFilesInDirectory(File directory) {
		if (directory.exists() && directory.isDirectory()) {
			File[] files = directory.listFiles();
			if (files != null) {
				for (File file : files) {
					if (file.isFile()) {
						file.delete(); // 파일 삭제
					}
				}
			}
		}
	}

	@PatchMapping("/Updateuserinfo")
	public HashMap<String, Object> updateuserinfo(@RequestBody HashMap<String, Object> map,
			@RequestHeader("Authorization") String token) {
		String removedToken = removeBearer(token);
		String uno = tokenProvider.getUserNumberFromToken(removedToken);
		map.put("uno", uno);

		int userType = tokenProvider.getUserTypeFromToken(removedToken);

		if (userType == 0) {
			usersService.updateUserInfo(map);
		} else if (userType == 1) {
			kakaoService.updateKakaoInfo(map);
		} else {
			map.clear();
			map.put("msg", "유효하지 않은 계정 정보입니다.");
			return map;
		}
		map.put("msg", "업데이트 완료");
		return map;
	}

	@PostMapping("/CheckUser")
	public HashMap<String, Object> checkPassword(@RequestBody HashMap<String, Object> map,
			@RequestHeader("Authorization") String token) {
		System.out.println(map);
		HashMap<String, Object> result = new HashMap<>();
		String uno = tokenProvider.getUserNumberFromToken(removeBearer(token));
		map.put("uno", uno);
		int checkedUser = usersService.checkUser(map);
		if (checkedUser >= 1) {
			result.put("status", true);
			result.put("msg", "계정 확인 성공");
			return result;
		} else {
			result.put("status", false);
			result.put("msg", "계정 확인 실패");
			return result;
		}
	}

	@PostMapping("/UpdatePassword")
	public Map<String, Object> updatePassword(@RequestBody Map<String, Object> map,
			@RequestHeader("Authorization") String token) {
		Map<String, Object> result = new HashMap<>();
		String uno = tokenProvider.getUserNumberFromToken(removeBearer(token));
		map.put("uno", uno);
		int work = usersService.updatePassword(map);
		if (work >= 1) {
			result.put("msg", "비밀번호 변경 완료");
		} else {
			result.put("msg", "비밀번호 변경 실패");
		}
		return result;
	}

	@PostMapping("/DeleteUser")
	public HashMap<String, Object> deleteUser(@RequestHeader("Authorization") String token) {
		HashMap<String, Object> result = new HashMap<>();
		String uno = tokenProvider.getUserNumberFromToken(removeBearer(token));
		int del = usersService.deleteUno(uno);
		if (del == 1) {
			result.put("msg", "회원 삭제 완료");
			return result;
		} else {
			result.put("msg", "회원 삭제 실패");
			return result;
		}
	}

	private String removeBearer(String token) {
		return token != null && token.startsWith("Bearer ") ? token.substring("Bearer ".length()).trim() : null;
	}

	@PostMapping("/findPassword")
	public Map<String, Object> findPassword(@RequestBody Map<String, String> body) {
		HashMap<String, Object> map = new HashMap<>();
		String uno = body.get("uno");
		String pwd = body.get("pwd");
		int result = usersService.findPassword(uno, pwd);
		if (result == 1) {
			map.put("msg", "비밀번호가 변경되었습니다.");
			map.put("code", 1);
		} else {
			map.put("msg", "비밀번호 변경에 실패하였습니다.");
			map.put("code", 2);
		}
		return map;
	}
}