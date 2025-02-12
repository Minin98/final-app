package com.kh.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import com.kh.dto.VideoDTO;
import com.kh.mapper.VideoMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class VideoService {
 
    @Value("${youtube.api.key}")
    private String apiKey;

    private final VideoMapper mapper;
    private final RestTemplate restTemplate = new RestTemplate();

    // 해당 챕터의 영상 목록 조회
    public List<VideoDTO> selectVideo(int chapterNumber) {
        try {
            return mapper.selectVideo(chapterNumber);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("영상 조회 실패 - chapterNumber: " + chapterNumber);
            return new ArrayList<>();
        }
    }

    // 특정 영상 정보 조회
    public VideoDTO getVideoByNumber(int videoNumber) {
        VideoDTO video = mapper.getVideoByNumber(videoNumber);
        if (video == null) {
            System.err.println("해당 videoNumber에 대한 영상 없음: " + videoNumber);
        }
        return video;
    }
    

    // 특정 영상의 이전/다음 영상 및 챕터 정보를 가져옴
    public Map<String, Object> getPrevNextVideo(int videoNumber, int chapterNumber, int classNumber) {
        Map<String, Object> videoData = new HashMap<>();
        VideoDTO video = mapper.getVideoByNumber(videoNumber);

        if (video == null) {
            videoData.put("code", 0);
            videoData.put("message", "해당 videoNumber에 대한 영상 없음");
            return videoData;
        }

        videoData.put("video", video);
        videoData.put("code", 1);
        videoData.put("message", "영상 조회 성공");

        Map<String, Object> params = new HashMap<>();
        params.put("videoNumber", videoNumber);
        params.put("chapterNumber", chapterNumber);
        params.put("classNumber", classNumber);

        // 이전 영상 찾기 (없으면 null 반환)
        Integer prevVideoId = mapper.getPrevVideoNumber(params);
        if (prevVideoId == null) {
            Integer prevChapter = mapper.getPrevChapter(params);
            if (prevChapter != null) {
                params.put("chapterNumber", prevChapter);
                prevVideoId = mapper.getLastVideoOfChapter(params);
            }
        }
        videoData.put("prevVideoId", prevVideoId); // null 반환 가능

        // 다음 영상 찾기 (없으면 null 반환)
        Integer nextVideoId = mapper.getNextVideoNumber(params);
        if (nextVideoId == null) {
            Integer nextChapter = mapper.getNextChapter(params);
            if (nextChapter != null) {
                params.put("chapterNumber", nextChapter);
                nextVideoId = mapper.getFirstVideoOfChapter(params);
            }
        }
        videoData.put("nextVideoId", nextVideoId); // null 반환 가능

        return videoData;
    }

    // 유튜브 URL에서 Video ID 추출
    private String extractVideoId(String videoUrl) {
        try {
            String regex = "^(?:https?:\\/\\/)?(?:www\\.)?(?:youtube\\.com\\/(?:[^\\/]+\\/.*\\/|(?:v|e(?:mbed)?)\\/|.*[?&]v=)|youtu\\.be\\/)([^\"&?\\/\\s]{11})";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(videoUrl);
            return matcher.find() ? matcher.group(1) : null;
        } catch (Exception e) {
            System.err.println("유튜브 ID 추출 실패: " + e.getMessage());
            return null;
        }
    }

    // 유튜브 API를 통해 영상 길이 가져오기
    private int getVideoDuration(String videoId) {
        try {
            String apiUrl = UriComponentsBuilder
                    .fromHttpUrl("https://www.googleapis.com/youtube/v3/videos")
                    .queryParam("id", videoId)
                    .queryParam("part", "contentDetails")
                    .queryParam("key", apiKey)
                    .toUriString();

            Map<String, Object> response = restTemplate.getForObject(apiUrl, Map.class);
            if (response == null || !response.containsKey("items")) {
                System.err.println("유튜브 API 응답 없음");
                return 0;
            }

            List<Map<String, Object>> items = (List<Map<String, Object>>) response.get("items");
            if (items.isEmpty()) {
                System.err.println("유튜브 API에서 해당 videoId의 정보 없음");
                return 0;
            }

            String duration = (String) ((Map<String, Object>) items.get(0).get("contentDetails")).get("duration");
            return duration != null ? convertISO8601ToSeconds(duration) : 0;
        } catch (Exception e) {
            System.err.println("유튜브 API 요청 실패: " + e.getMessage());
            return 0;
        }
    }

    // 유튜브 영상 등록 (URL 입력받아 자동 등록)
    public boolean insertVideo(int classNumber, int chapterNumber, String videoTitle, String videoUrl) {
        String videoId = extractVideoId(videoUrl);
        if (videoId == null) {
            System.err.println("유효하지 않은 유튜브 URL: " + videoUrl);
            return false;
        }

        int videoDuration = getVideoDuration(videoId);
        if (videoDuration == 0) {
            System.err.println("유효하지 않은 영상 길이: " + videoUrl);
            return false;
        }

        VideoDTO video = new VideoDTO(0, classNumber, chapterNumber, videoTitle, videoId, videoDuration, 0, null);
        return mapper.insertVideo(video) > 0;
    }

    // ISO 8601 형식의 시간을 초 단위로 변환
    private int convertISO8601ToSeconds(String duration) {
        Pattern pattern = Pattern.compile("PT(?:(\\d+)H)?(?:(\\d+)M)?(?:(\\d+)S)?");
        Matcher matcher = pattern.matcher(duration);
        int hours = 0, minutes = 0, seconds = 0;

        if (matcher.matches()) {
            if (matcher.group(1) != null)
                hours = Integer.parseInt(matcher.group(1));
            if (matcher.group(2) != null)
                minutes = Integer.parseInt(matcher.group(2));
            if (matcher.group(3) != null)
                seconds = Integer.parseInt(matcher.group(3));
        }
        return hours * 3600 + minutes * 60 + seconds;
    }
}
