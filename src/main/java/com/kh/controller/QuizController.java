
package com.kh.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.kh.dto.QuizDTO;
import com.kh.service.ChapterService;
import com.kh.service.ClassService;
import com.kh.service.QuizService;
import com.kh.service.VideoService;
import com.kh.token.JwtTokenProvider;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
public class QuizController {
	private final QuizService quizService;

	@PostMapping("/quiz/insert")
	public Map<String, Object> insertQuiz(@RequestBody Map<String, Object> body) {
		Map<String, Object> result = new HashMap<>();

		// 1. chapterNumber 추출
		int chapterNumber = (int) body.get("chapterNumber");
		System.out.println(chapterNumber);
		// 2. quizzes 리스트 추출 (List<Map<String, Object>> 형태로 받아야 함)
		List<Map<String, Object>> quizzesData = (List<Map<String, Object>>) body.get("quizzes");

		// 3. Map을 QuizDTO 리스트로 변환
		List<QuizDTO> quizList = new ArrayList<>();
		for (Map<String, Object> quizData : quizzesData) {
			QuizDTO quiz = new QuizDTO(0, // quizId (자동 생성이면 0)
					chapterNumber, (int) quizData.get("quizNumber"), (String) quizData.get("question"),
					(String) quizData.get("answer"), (String) quizData.get("description"));
			quizList.add(quiz);
		}
		System.out.println(quizList);
		// 4. 서비스에 넘겨서 저장
		int count = quizService.insertQuizzes(quizList);
		if (count == 0) {
			result.put("msg", "퀴즈 등록 실패");
			return result;
		}
		result.put("msg", "퀴즈 등록 성공");
		return result;
	}

	@DeleteMapping("/quiz/{chapterNumber}")
	public Map<String, Object> deleteQuiz(@PathVariable int chapterNumber) {
		Map<String, Object> map = new HashMap<>();
		int count = quizService.deleteQuiz(chapterNumber);
		if (count == 0) {
			map.put("msg", "퀴즈 삭제 실패");
			return map;
		}
		map.put("msg", "퀴즈 삭제 성공");
		return map;
	}

	@GetMapping("/quiz/list/{chapterNumber}")
	public Map<String, List<QuizDTO>> selectQuizList(@PathVariable int chapterNumber) {
		Map<String, List<QuizDTO>> result = new HashMap<>();
		List<QuizDTO> quizList = quizService.selectQuizList(chapterNumber);
		result.put("quizList", quizList);
		return result;
	}

	@PostMapping("/quiz/update")
	public Map<String, Object> updateQuiz(@RequestBody Map<String, Object> body) {
		Map<String, Object> result = new HashMap<>();

		// 1. chapterNumber 추출
		int chapterNumber = (int) body.get("chapterNumber");
		System.out.println(chapterNumber);
		quizService.deleteQuiz(chapterNumber);
		// 2. quizzes 리스트 추출 (List<Map<String, Object>> 형태로 받아야 함)
		List<Map<String, Object>> quizzesData = (List<Map<String, Object>>) body.get("quizzes");

		// 3. Map을 QuizDTO 리스트로 변환
		List<QuizDTO> quizList = new ArrayList<>();
		for (Map<String, Object> quizData : quizzesData) {
			QuizDTO quiz = new QuizDTO(0, // quizId (자동 생성이면 0)
					chapterNumber, (int) quizData.get("quizNumber"), (String) quizData.get("question"),
					(String) quizData.get("answer"), (String) quizData.get("description"));
			quizList.add(quiz);
		}
		System.out.println(quizList);
		// 4. 서비스에 넘겨서 저장
		int count = quizService.insertQuizzes(quizList);
		if (count == 0) {
			result.put("msg", "퀴즈 수정 실패");
			return result;
		}
		result.put("msg", "퀴즈 수정 완료");
		return result;
	}

}
