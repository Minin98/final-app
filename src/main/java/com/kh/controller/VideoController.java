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

    // 유튜브 영상 등록
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
        response.put("code", success ? 1 : 0);
        response.put("message", success ? "영상 등록 성공" : "영상 등록 실패 (잘못된 유튜브 URL 또는 영상 길이 오류)");

        return response;
    }

    // 특정 영상 조회
    @GetMapping("/{videoNumber}")
    public Map<String, Object> getVideo(@PathVariable int videoNumber) {
        Map<String, Object> response = new HashMap<>();

        VideoDTO video = videoService.getVideoByNumber(videoNumber);
        if (video == null) {
            response.put("code", 0);
            response.put("message", "해당 videoNumber에 대한 영상 없음");
            return response;
        }

        response.put("video", video);
        response.put("classTitle", video.getClassTitle() != null ? video.getClassTitle() : "강의 정보 없음");
        response.put("chapterTitle", video.getChapterTitle() != null ? video.getChapterTitle() : "챕터 정보 없음");
        response.put("code", 1);
        response.put("message", "영상 조회 성공");

        return response;
    }

    // 이전/다음 영상 조회 API 추가
    @GetMapping("/{videoNumber}/navigation/{classNumber}")
    public Map<String, Object> getPrevNextVideo(@PathVariable int videoNumber, @PathVariable int classNumber) {
        return videoService.getPrevNextVideo(videoNumber, classNumber);
    }
}
