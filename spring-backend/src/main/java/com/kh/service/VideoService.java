package com.kh.service;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kh.dto.VideoDTO;
import com.kh.mapper.VideoMapper;

@Service
public class VideoService {
    @Autowired
    private VideoMapper mapper;

    public VideoService(VideoMapper mapper) {
        this.mapper = mapper;
    }

    public List<VideoDTO> selectVideo(int chapterNumber) {
        return mapper.selectVideo(chapterNumber);
    }

    
    
}
