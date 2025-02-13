package com.kh.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.kh.dto.QuizDTO;
import com.kh.mapper.QuizMapper;

@Service
public class QuizService {
	private final QuizMapper quizMapper;

	public QuizService(QuizMapper quizMapper) {
		this.quizMapper = quizMapper;
	}

	public int insertQuizzes(List<QuizDTO> quizList) {
		int count = 1;
	    try {
	        quizList.forEach(item -> {
	            quizMapper.insertQuizItem(item);  // 항목 삽입
	        });
	        return count;
	    } catch (Exception e) {
	        // 삽입 중 예외 발생 시 처리
	        System.out.println("퀴즈 등록에 실패했습니다: " + e.getMessage());
	        return count = 0;
	    }
	}

	public int deleteQuiz(int chapterNumber) {
		return quizMapper.deleteQuiz(chapterNumber);
	}

	public List<QuizDTO> selectQuizList(int chapterNumber) {
		return quizMapper.selectQuizList(chapterNumber);
	}
}