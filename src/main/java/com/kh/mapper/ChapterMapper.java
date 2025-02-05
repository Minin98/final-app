package com.kh.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.kh.dto.ChapterDTO;

@Mapper
public interface ChapterMapper {

    List<ChapterDTO> selectChapter(int classNumber);

    
}