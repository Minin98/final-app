package com.kh.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.kh.service.VideoService;
import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
@RequestMapping("/video")
public class VideoController {

    private final VideoService videoService;

    // 영상을 등록하는 API
    @PostMapping("/insert")
    public Map<String, Object> insertVideo(@RequestBody Map<String, String> requestData) {
        Map<String, Object> response = new HashMap<>();
        String videoId = requestData.get("videoId");
        String videoTitle = requestData.get("videoTitle"); // 강사가 입력한 제목을 받음
        int chapterNumber;

        try {
            chapterNumber = Integer.parseInt(requestData.get("chapterNumber"));
        } catch (NumberFormatException e) {
            response.put("code", 0);
            response.put("message", "Invalid chapter number");
            return response;
        }

        boolean success = videoService.insertVideo(chapterNumber, videoTitle, videoId);
        if (success) {
            response.put("code", 1);
            response.put("message", "영상 등록 성공");
        } else {
            response.put("code", 0);
            response.put("message", "영상 등록 실패 (잘못된 videoId)");
        }
        return response;
    }
}
