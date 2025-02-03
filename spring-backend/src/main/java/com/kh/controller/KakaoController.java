package com.kh.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.kh.dto.KakaoDTO;
import com.kh.service.UsersService;
import com.kh.token.JwtTokenProvider;

import jakarta.servlet.http.HttpSession;

@CrossOrigin(allowedHeaders = "*", origins = "*")
@RestController
public class KakaoController {
	private final String REST_API_KEY = "aacd85e03b5e9a1d0876e649521fbbd1";
	private final String REDIRECT_URI = "http://localhost:3000/kakao/login/oauth";
	private final UsersService usersService;
	private final JwtTokenProvider tokenProvider;

	public KakaoController(UsersService usersService, JwtTokenProvider tokenProvider) {
		super();
		this.usersService = usersService;
		this.tokenProvider = tokenProvider;
	}

	@GetMapping("/kakao/token")
	public Map<String, Object> kakaoCallBack(String code, String role) throws JSONException {
		Map<String, Object> result = new HashMap<>();
		System.out.println("Received role: " + role);
		String apiURL = "https://kauth.kakao.com/oauth/token?";
		apiURL += "grant_type=authorization_code" + "&client_id=" + REST_API_KEY + "&redirect_uri=" + REDIRECT_URI
				+ "&code=" + code;
		
		// API 요청을 통해 토큰을 받음
		String tokenResponse = requestKakaoServer(apiURL, null);

		if (tokenResponse != null && !tokenResponse.isEmpty()) {
			// JSON 파싱하여 access_token 추출
			JSONObject jsonObject = new JSONObject(tokenResponse);
			String accessToken = jsonObject.optString("access_token"); // access_token만 추출
			
			if (accessToken != null && !accessToken.isEmpty()) {
				// access_token이 유효한 경우 프로필 정보 요청
				String profile = getKakaoProfile(accessToken);
				if (profile != null && !profile.isEmpty()) {
					System.out.println("Profile Info: " + profile);
					// profile을 JSONObject로 변환
					JSONObject profileJson = new JSONObject(profile);
					String uno = profileJson.optString("id");
					String nickname = profileJson.getJSONObject("kakao_account").getJSONObject("profile").optString("nickname");
					int grade = "student".equals(role) ? 2 : 1; // role에 따라 grade 설정
					KakaoDTO dto = usersService.kakaoLogin(uno, nickname, grade);
					String token = tokenProvider.generateKakaoJwtToken(dto);
					result.put("msg", "로그인에 성공 하셨습니다.");
					result.put("token", token);
				} else {
					result.put("msg", "프로필 정보를 가져오지 못했습니다.");
				}
			} else {
				result.put("msg", "access_token을 추출할 수 없습니다.");
			}
		}
		return result;
	}

	/*
	 * @GetMapping("/kakao/profile") public String getProfile(String token) {
	 * System.out.println(token); String header = "Bearer " + token; String apiURL =
	 * "https://kapi.kakao.com/v2/user/me";
	 * 
	 * String res = requestKakaoServer(apiURL, header); System.out.println(res);
	 * return res; }
	 */
	
	@ResponseBody
	@PostMapping("/kakao/delete")
	public String deleteToken(@RequestBody String token) {
		System.out.println(token);
		String header = "Bearer " + token;
		String apiURL = "https://kapi.kakao.com/v1/user/unlink";

		String res = requestKakaoServer(apiURL, header);
		System.out.println(res);
		return res;
	}

	public String getKakaoProfile(String accessToken) {
		String header = "Bearer " + accessToken;
		String apiURL = "https://kapi.kakao.com/v2/user/me";

		return requestKakaoServer(apiURL, header);
	}

	public String requestKakaoServer(String apiURL, String header) {
		StringBuffer res = new StringBuffer();
		try {
			URL url = new URL(apiURL);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			if (header != null && !header.equals("")) {
				con.setRequestProperty("Authorization", header);
			}

			int responseCode = con.getResponseCode();
			BufferedReader br;
			System.out.print("responseCode=" + responseCode);
			if (responseCode == 200) { // 정상 호출
				br = new BufferedReader(new InputStreamReader(con.getInputStream()));
			} else { // 에러 발생
				br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
			}
			String inputLine;
			while ((inputLine = br.readLine()) != null) {
				res.append(inputLine);
			}
			br.close();
			if (responseCode == 200) {
				System.out.println(res.toString());
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		return res.toString();
	}

}
