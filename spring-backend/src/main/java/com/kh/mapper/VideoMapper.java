package com.kh.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.kh.dto.VideoDTO;

@Mapper
public interface VideoMapper {

    List<VideoDTO> selectVideo(int chapterNumber);

    
} 