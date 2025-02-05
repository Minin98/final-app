package com.kh.controller;

import com.kh.service.ChapterService;
import com.kh.service.ClassService;
import com.kh.service.VideoService;
import com.kh.token.JwtTokenProvider;

import io.jsonwebtoken.lang.Collections;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kh.dto.ChapterDTO;
import com.kh.dto.ClassDTO;
import com.kh.dto.VideoDTO;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.HttpHeaders;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/class")
public class ClassController {

    private final ClassService classService;
    private final ChapterService chapterService;
    private final VideoService videoService;
    private final JwtTokenProvider tokenProvider;

    public ClassController(ClassService classService, ChapterService chapterService, VideoService videoService, JwtTokenProvider tokenProvider) {
        this.classService = classService;
        this.chapterService = chapterService;
        this.videoService = videoService;
        this.tokenProvider = tokenProvider;
    }

    @GetMapping("/list")
    public List<ClassDTO> selectClassList(
            @RequestParam(required = false, defaultValue = "전체") String category,
            @RequestParam(required = false, defaultValue = "최신순") String sort) {
        return classService.selectClassList(category, sort);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ClassDTO>> searchClasses(
            @RequestParam(required = false) String searchKeyword,
            @RequestParam(required = false, defaultValue = "전체") String category,
            @RequestParam(required = false, defaultValue = "최신순") String sort) {

        try {
            List<ClassDTO> classList = classService.searchClasses(searchKeyword, category, sort);
            // System.out.println("🔍 검색된 강의 목록: " + classList);
            return ResponseEntity.ok(classList);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    // 썸네일 제공 API
    @GetMapping("/classThumbnails/{fileName}")
    public ResponseEntity<Resource> getClassThumbnail(@PathVariable String fileName) {
        String filePath = "C:\\classThumbnails\\" + fileName;
        System.out.println("요청된 파일 경로 : " + filePath);
        File file = new File(filePath);

        if (!file.exists()) {
            System.out.println("파일이 존재하지 않음 : " + filePath);
            return ResponseEntity.notFound().build();
        }

        Resource resource = new FileSystemResource(file);
        System.out.println("파일 제공: " + filePath);

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG) // 기본적으로 JPEG 설정 (필요 시 수정 가능)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getName() + "\"")
                .body(resource);
    }

    @PostMapping("/write")
    public Map<String, Object> classWrite(
            @RequestHeader("Authorization") String token,
            @RequestPart("params") String params,
            @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnailFile) {

        Map<String, Object> map = new HashMap<>();

<<<<<<< HEAD
        System.out.println(map);

        try {
            token = token != null ? token.replace("Bearer ", "") : null;
            System.out.println("try 들어옴 토큰 : "+token);

            String role = tokenProvider.getRoleFromToken(token);
            System.out.println("토큰 : "+role);

            int userGrade = "강사".equals(role) ? 1 : 2;
            System.out.println("강사?? : "+userGrade);
            
            
=======
        try {
            token = token != null ? token.replace("Bearer ", "") : null;
            String role = tokenProvider.getRoleFromToken(token);
            int userGrade = "강사".equals(role) ? 1 : 2;

>>>>>>> 751184ce14df6f290f9561852373def57bfaf751
            if (userGrade != 1) {
                map.put("code", 3);
                map.put("msg", "강사만 강의 등록이 가능합니다.");
                return map;
            }

            ObjectMapper objectMapper = new ObjectMapper();
            ClassDTO classDTO = objectMapper.readValue(params, ClassDTO.class);

            // 수신한 데이터 로그
<<<<<<< HEAD
             System.out.println(" 강의 데이터 확인:");
             System.out.println("제목: " + classDTO.getTitle());
             System.out.println("설명: " + classDTO.getDescription());
             System.out.println("카테고리: " + classDTO.getCategory());
=======
            // System.out.println(" 강의 데이터 확인:");
            // System.out.println("제목: " + classDTO.getTitle());
            // System.out.println("설명: " + classDTO.getDescription());
            // System.out.println("카테고리: " + classDTO.getCategory());
>>>>>>> 751184ce14df6f290f9561852373def57bfaf751

            if (thumbnailFile != null) {
                System.out.println("썸네일 파일 확인: " + thumbnailFile.getOriginalFilename());
            } else {
                System.out.println(" 썸네일 파일 없음");
            }

            // 썸네일 파일 저장
            if (thumbnailFile != null && !thumbnailFile.isEmpty()) {
                String uploadDir = "C:\\classThumbnails";
                File dir = new File(uploadDir);
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                String fileName = UUID.randomUUID() + "_" + thumbnailFile.getOriginalFilename();
                String filePath = uploadDir + File.separator + fileName;

                System.out.println("저장할 파일 경로 : " + filePath);

                File file = new File(filePath);
                thumbnailFile.transferTo(file);

                classDTO.setThumbnail(fileName);
            }

            int classNumber = classService.insertClass(classDTO);
            map.put("classNumber", classNumber);
            map.put("code", 1);
            map.put("msg", "강의 등록 성공");

        } catch (Exception e) {
            e.printStackTrace();
            map.put("code", 2);
            map.put("msg", "강의 등록 실패");
        }

        return map;
    }

<<<<<<< HEAD
    @GetMapping("/{classNumber}")
=======
    @GetMapping("/class/{classNumber}")
>>>>>>> 751184ce14df6f290f9561852373def57bfaf751
    public Map<String, Object> classView(@PathVariable int classNumber) {
        Map<String, Object> map = new HashMap<>();
        ClassDTO classDTO = classService.selectClass(classNumber);
        List<ChapterDTO> chapters = chapterService.selectChapter(classNumber);

        if (chapters == null) {
            chapters = new ArrayList<>();
        }

        // 챕터별 영상 목록을 저장할 Map
        Map<Integer, List<VideoDTO>> videoMap = new HashMap<>();

        for (ChapterDTO chapter : chapters) {
            int chapterNumber = chapter.getChapterNumber();
            List<VideoDTO> videos = videoService.selectVideo(chapterNumber);
            videoMap.put(chapterNumber, videos);
        }

        map.put("class", classDTO);
        map.put("chapter", chapters);
        map.put("video", videoMap);
        return map;
    }

}