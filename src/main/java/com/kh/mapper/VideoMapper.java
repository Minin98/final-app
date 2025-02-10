package com.kh.mapper;

import org.apache.ibatis.annotations.Mapper;
import com.kh.dto.VideoDTO;
import java.util.List;

@Mapper
public interface VideoMapper {

    // 해당 챕터의 영상 목록 조회
    List<VideoDTO> selectVideo(int chapterNumber);

    // 유튜브 영상 정보 DB에 저장
    int insertVideo(VideoDTO video);
}
