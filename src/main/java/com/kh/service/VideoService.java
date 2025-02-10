package com.kh.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import com.kh.dto.VideoDTO;
import com.kh.mapper.VideoMapper;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class VideoService {

    @Value("${youtube.api.key}")
    private String apiKey; // application-API-KEY.properties에서 API 키 가져오기

    private final RestTemplate restTemplate = new RestTemplate(); // HTTP 요청을 위한 RestTemplate 객체
    private final VideoMapper mapper; // MyBatis를 통한 DB 접근

    // 해당 챕터의 영상 목록 조회 (기존 기능 유지)
    public List<VideoDTO> selectVideo(int chapterNumber) {
        return mapper.selectVideo(chapterNumber);
    }

    // 유튜브 API를 통해 영상 ID, 길이 가져오기
    private int getVideoDuration(String videoId) {
        String apiUrl = UriComponentsBuilder
                .fromHttpUrl("https://www.googleapis.com/youtube/v3/videos")
                .queryParam("id", videoId)
                .queryParam("part", "contentDetails")
                .queryParam("key", apiKey)
                .toUriString();

        try {
            Map<String, Object> response = restTemplate.getForObject(apiUrl, Map.class);
            if (response != null && response.containsKey("items")) {
                var items = (List<Map<String, Object>>) response.get("items");
                if (!items.isEmpty()) {
                    var contentDetails = (Map<String, Object>) items.get(0).get("contentDetails");
                    return convertISO8601ToSeconds(contentDetails.get("duration").toString()); // 초 단위 변환
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0; // 오류 발생 시 0초 반환
    }

    // 영상을 등록할 때 API를 통해 영상 길이 , 영상ID 가져와 DB에 저장
    public boolean insertVideo(int chapterNumber, String videoTitle, String videoId) {
        int videoDuration = getVideoDuration(videoId); // 영상 길이 가져오기
        if (videoDuration == 0) {
            return false; // 유효한 영상이 아닐 경우 저장하지 않음
        }

        VideoDTO video = new VideoDTO(0, chapterNumber, videoTitle, videoId, videoDuration, 0, null);
        return mapper.insertVideo(video) > 0; // DB에 저장 성공 여부 반환
    }

    // ISO 8601 형식의 시간(PnYnMnDTnHnMnS)을 초 단위로 변환
    private int convertISO8601ToSeconds(String duration) {
        Pattern pattern = Pattern.compile("PT(?:(\\d+)H)?(?:(\\d+)M)?(?:(\\d+)S)?");
        Matcher matcher = pattern.matcher(duration);
        int hours = 0, minutes = 0, seconds = 0;

        if (matcher.matches()) {
            if (matcher.group(1) != null) hours = Integer.parseInt(matcher.group(1));
            if (matcher.group(2) != null) minutes = Integer.parseInt(matcher.group(2));
            if (matcher.group(3) != null) seconds = Integer.parseInt(matcher.group(3));
        }
        return hours * 3600 + minutes * 60 + seconds;
    }
}
