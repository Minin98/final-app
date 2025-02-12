package com.kh.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.kh.dto.VideoDTO;
import com.kh.service.VideoService;
import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
@RequestMapping("/video")
public class VideoController {

    private final VideoService videoService;

    // 영상 등록 API
    @PostMapping("/insert")
    public Map<String, Object> insertVideo(@RequestBody Map<String, String> requestData) {
        Map<String, Object> response = new HashMap<>();
        String videoUrl = requestData.get("videoUrl"); // 유튜브 URL
        String videoTitle = requestData.get("videoTitle"); // 영상 제목
        int chapterNumber;
        int classNumber;

        try {
            chapterNumber = Integer.parseInt(requestData.get("chapterNumber"));
            classNumber = Integer.parseInt(requestData.get("classNumber"));
        } catch (NumberFormatException e) {
            response.put("code", 0);
            response.put("message", "Invalid chapter number or class number");
            return response;
        }

        boolean success = videoService.insertVideo(classNumber, chapterNumber, videoTitle, videoUrl);
        if (success) {
            response.put("code", 1);
            response.put("message", "영상 등록 성공");
        } else {
            response.put("code", 0);
            response.put("message", "영상 등록 실패 (잘못된 유튜브 URL 또는 영상 길이 오류)");
        }
        return response;
    }

    // 영상 조회 API (이전/다음 영상 포함)
    @GetMapping("/{videoNumber}")
    public Map<String, Object> getVideo(@PathVariable int videoNumber) {
        Map<String, Object> response = new HashMap<>();
    
        VideoDTO video = videoService.getVideoByNumber(videoNumber);
        if (video == null) {
            response.put("code", 0);
            response.put("message", "해당 videoNumber에 대한 영상 없음");
            return response;
        }
    
        System.out.println("video.getClassTitle(): " + video.getClassTitle());
        System.out.println("video.getChapterTitle(): " + video.getChapterTitle());
    
        int chapterNumber = video.getChapterNumber();
        int classNumber = video.getClassNumber();
    
        Map<String, Object> videoData = videoService.getPrevNextVideo(videoNumber, chapterNumber, classNumber);
    
        videoData.put("prevVideoId", videoData.get("prevVideoId") != null && (int) videoData.get("prevVideoId") == 0 ? null : videoData.get("prevVideoId"));
        videoData.put("nextVideoId", videoData.get("nextVideoId") != null && (int) videoData.get("nextVideoId") == 0 ? null : videoData.get("nextVideoId"));
    
        response.put("video", video);
        response.put("classTitle", video.getClassTitle() != null ? video.getClassTitle() : "강의 정보 없음");
        response.put("chapterTitle", video.getChapterTitle() != null ? video.getChapterTitle() : "챕터 정보 없음");
        response.put("code", 1);
        response.put("message", "영상 조회 성공");
    
        return response;
    }
    
}
