package com.kh.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.kh.dto.ChapterDTO;

@Mapper
public interface ChapterMapper {

    // 특정 강의의 챕터 목록 조회
    List<ChapterDTO> selectChapter(@Param("classNumber") int classNumber);

    // 새 챕터 번호 생성
    int selectChapterNo();

    // 챕터 등록
    int insertChapter(ChapterDTO chapter);
}
