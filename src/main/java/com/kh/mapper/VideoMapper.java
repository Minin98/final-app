package com.kh.mapper;

import org.apache.ibatis.annotations.Mapper;
import com.kh.dto.VideoDTO;
import java.util.List;
import java.util.Map;

@Mapper
public interface VideoMapper {

    // 해당 챕터의 영상 목록 조회
    List<VideoDTO> selectVideo(int chapterNumber);

    // 유튜브 영상 정보 DB에 저장
    int insertVideo(VideoDTO video);

    // videoNumber로 영상 조회
    VideoDTO getVideoByNumber(int videoNumber);

    Integer getPrevVideoNumber(Map<String, Object> params);

    Integer getNextVideoNumber(Map<String, Object> params);

    Integer getPrevChapter(Map<String, Object> params);

    Integer getNextChapter(Map<String, Object> params);

    Integer getLastVideoOfChapter(Map<String, Object> params);

    Integer getFirstVideoOfChapter(Map<String, Object> params);
}
