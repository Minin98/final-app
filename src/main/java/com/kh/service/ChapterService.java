package com.kh.service;

import java.util.List;

import org.springframework.stereotype.Service;
import com.kh.dto.ChapterDTO;
import com.kh.mapper.ChapterMapper;

@Service
public class ChapterService {

    private final ChapterMapper mapper;

    public ChapterService(ChapterMapper mapper) {
        this.mapper = mapper;
    }

    // 챕터 등록
    public int insertChapter(ChapterDTO chapter) {
        int chapterNumber = mapper.selectChapterNo(); // 새로운 챕터 번호 생성
        chapter.setChapterNumber(chapterNumber);
        return mapper.insertChapter(chapter) > 0 ? chapterNumber : -1;
    }

    // 특정 강의의 챕터 목록 조회
    public List<ChapterDTO> selectChapter(int classNumber) {
        return mapper.selectChapter(classNumber);
    }
}
