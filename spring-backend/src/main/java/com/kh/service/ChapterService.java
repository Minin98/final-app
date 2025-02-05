package com.kh.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kh.dto.ChapterDTO;
import com.kh.mapper.ChapterMapper;

@Service
public class ChapterService {
    
    @Autowired
    private ChapterMapper mapper;

    public List<ChapterDTO> selectChapter(int classNumber) {
        return mapper.selectChapter(classNumber);
    }
}
