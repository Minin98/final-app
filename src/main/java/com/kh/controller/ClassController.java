package com.kh.controller;

import com.kh.service.ChapterService;
import com.kh.service.ClassService;
import com.kh.service.QuizService;
import com.kh.service.VideoService;
import com.kh.token.JwtTokenProvider;

import io.jsonwebtoken.lang.Collections;
import lombok.RequiredArgsConstructor;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kh.dto.ChapterDTO;
import com.kh.dto.ClassDTO;
import com.kh.dto.VideoDTO;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
@RequestMapping("/class")
public class ClassController {

    private final ClassService classService;
    private final ChapterService chapterService;
    private final VideoService videoService;
    private final QuizService quizService;
    private final JwtTokenProvider tokenProvider;

    private final String uploadDir = "C:/classThumbnails/";

    /** ✅ 강의 목록 조회 */
    @GetMapping("/list")
    public List<ClassDTO> selectClassList(
            @RequestParam(required = false, defaultValue = "전체") String category,
            @RequestParam(required = false, defaultValue = "최신순") String sort) {
        return classService.selectClassList(category, sort);
    }

    /** ✅ 강의 검색 API */
    @GetMapping("/search")
    public Map<String, Object> searchClasses(
            @RequestParam(required = false) String searchKeyword,
            @RequestParam(required = false, defaultValue = "전체") String category,
            @RequestParam(required = false, defaultValue = "최신순") String sort) {

        Map<String, Object> map = new HashMap<>();
        try {
            List<ClassDTO> classList = classService.searchClasses(searchKeyword, category, sort);
            System.out.println("검색 요청 - 키워드: " + searchKeyword + ", 카테고리: " + category + ", 정렬: " + sort);
            map.put("classList", classList);
            map.put("code", 1);
        } catch (Exception e) {
            e.printStackTrace();
            map.put("code", 2);
            map.put("msg", "강의 검색 실패");
        }
        return map;
    }

    /** ✅ 썸네일 제공 */
    @GetMapping("/thumbnail/{filename}")
    public ResponseEntity<Resource> getThumbnail(@PathVariable String filename) {
        try {
            Path filePath = Paths.get(uploadDir).resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /** ✅ 강의 등록 */
    @PostMapping("/write")
    public Map<String, Object> classWrite(
            @RequestHeader("Authorization") String token,
            @RequestPart("params") String params,
            @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnailFile) {

        Map<String, Object> map = new HashMap<>();
        try {
            token = token.replace("Bearer ", "");
            int userGrade = tokenProvider.getRoleFromToken(token);

            if (userGrade != 1) {
                map.put("code", 3);
                map.put("msg", "강사만 강의 등록이 가능합니다.");
                return map;
            }

            ObjectMapper objectMapper = new ObjectMapper();
            ClassDTO classDTO = objectMapper.readValue(params, ClassDTO.class);

            if (thumbnailFile != null && !thumbnailFile.isEmpty()) {
                String savedFileName = UUID.randomUUID() + "_" + thumbnailFile.getOriginalFilename();
                File savedFile = new File(uploadDir, savedFileName);
                thumbnailFile.transferTo(savedFile);
                classDTO.setThumbnail(savedFileName);
            }

            int classNumber = classService.insertClass(classDTO);
            map.put("classNumber", classNumber);
            map.put("thumbnail", classDTO.getThumbnail());
            map.put("code", 1);
            map.put("msg", "강의 등록 성공");

        } catch (Exception e) {
            e.printStackTrace();
            map.put("code", 2);
            map.put("msg", "강의 등록 실패");
        }
        return map;
    }

    /** ✅ 강의 상세 조회 */
    @GetMapping("/{classNumber}")
    public Map<String, Object> classView(@PathVariable int classNumber) {
        Map<String, Object> map = new HashMap<>();
        ClassDTO classDTO = classService.selectClass(classNumber);
        List<ChapterDTO> chapters = chapterService.selectChapter(classNumber);

        if (chapters == null) {
            chapters = new ArrayList<>();
        }

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

    /** ✅ 강의 수정 */
    @PostMapping("/update")
    public Map<String, Object> classUpdate(
            @RequestHeader("Authorization") String token,
            @RequestPart("params") String params,
            @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnailFile) {

        Map<String, Object> map = new HashMap<>();
        try {
            token = token.replace("Bearer ", "");
            int userGrade = tokenProvider.getRoleFromToken(token);

            if (userGrade != 1) {
                map.put("code", 3);
                map.put("msg", "강사만 강의를 수정할 수 있습니다.");
                return map;
            }

            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, String> paramsMap = objectMapper.readValue(params, new TypeReference<>() {});

            ClassDTO dto = new ClassDTO();
            dto.setClassNumber(Integer.parseInt(paramsMap.get("classNumber")));
            dto.setTitle(paramsMap.get("title"));
            dto.setDescription(paramsMap.get("description"));
            dto.setCategory(paramsMap.get("category"));

            if (thumbnailFile != null && !thumbnailFile.isEmpty()) {
                String fileName = UUID.randomUUID() + "_" + thumbnailFile.getOriginalFilename();
                File file = new File(uploadDir, fileName);
                thumbnailFile.transferTo(file);
                dto.setThumbnail(fileName);
            }

            classService.updateClass(dto);
            map.put("code", 1);
            map.put("msg", "강의 수정 성공");

        } catch (Exception e) {
            e.printStackTrace();
            map.put("code", 2);
            map.put("msg", "강의 수정 실패");
        }
        return map;
    }

    /** ✅ 강의 삭제 */
    @DeleteMapping("/{classNumber}")
    public Map<String, Object> classDelete(@RequestHeader("Authorization") String token, @PathVariable int classNumber) {
        Map<String, Object> map = new HashMap<>();

        try {
            token = token.replace("Bearer ", "");
            String userNumber = tokenProvider.getUserNumberFromToken(token);
            String classOwner = classService.selectClass(classNumber).getUno();

            if (userNumber.equals(classOwner)) {
                classService.deleteClass(classNumber);
                map.put("code", 1);
                map.put("msg", "강의 삭제 완료");
            } else {
                map.put("code", 2);
                map.put("msg", "삭제 권한이 없습니다.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            map.put("code", 3);
            map.put("msg", "강의 삭제 중 오류 발생");
        }

        return map;
    }
}
