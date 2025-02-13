package com.kh.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.kh.dto.QuizDTO;

@Mapper
public interface QuizMapper {

	void insertQuizItem(QuizDTO item);

	int deleteQuiz(int chapterNumber);

	List<QuizDTO> selectQuizList(int chapterNumber);

}