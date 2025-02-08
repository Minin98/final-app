package com.kh.controller;

import com.kh.service.ChapterService;
import com.kh.service.ClassService;
import com.kh.service.VideoService;
import com.kh.token.JwtTokenProvider;

import io.jsonwebtoken.lang.Collections;
import lombok.RequiredArgsConstructor;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kh.dto.ChapterDTO;
import com.kh.dto.ClassDTO;
import com.kh.dto.VideoDTO;

import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;

import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor // 생성자 자동생성 어노테이션 Lombok 에서 제공 생성자 작성필요없어짐
@RequestMapping("/class")
public class ClassController {

    private final ClassService classService;
    private final ChapterService chapterService;
    private final VideoService videoService;
    private final JwtTokenProvider tokenProvider;

    private final String uploadDir = "C:/classThumbnails/"; // 이미지 저장 경로

    @GetMapping("/list")
    public List<ClassDTO> selectClassList(
            @RequestParam(required = false, defaultValue = "전체") String category,
            @RequestParam(required = false, defaultValue = "최신순") String sort) {
        return classService.selectClassList(category, sort);
    }

    @GetMapping("/search")
    public Map<String, Object> searchClasses(
            @RequestParam(required = false) String searchKeyword,
            @RequestParam(required = false, defaultValue = "전체") String category,
            @RequestParam(required = false, defaultValue = "최신순") String sort) {

        Map<String, Object> map = new HashMap<>();

        try {
            // 정렬 값 확인
            System.out.println("검색 요청 - 키워드: " + searchKeyword + ", 카테고리: " + category + ", 정렬: " + sort);

            List<ClassDTO> classList = classService.searchClasses(searchKeyword, category, sort);
            map.put("classList", classList);
            map.put("code", 1);
        } catch (Exception e) {
            e.printStackTrace();
            map.put("code", 2);
            map.put("msg", "강의 검색 실패");
        }
        return map;
    }

    @PostMapping("/write")
    public Map<String, Object> classWrite(
            @RequestHeader("Authorization") String token,
            @RequestPart("params") String params,
            @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnailFile) {

        Map<String, Object> map = new HashMap<>();

        try {
            token = token != null ? token.replace("Bearer ", "") : null;
            int userGrade = tokenProvider.getRoleFromToken(token);

            if (userGrade != 1) {
                map.put("code", 3);
                map.put("msg", "강사만 강의 등록이 가능합니다.");
                return map;
            }

            ObjectMapper objectMapper = new ObjectMapper();
            ClassDTO classDTO = objectMapper.readValue(params, ClassDTO.class);

            String savedFileName = null;

            // 썸네일 파일 저장
            if (thumbnailFile != null && !thumbnailFile.isEmpty()) {
                String uploadDir = "C:\\classThumbnails";
                File dir = new File(uploadDir);
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                savedFileName = UUID.randomUUID() + "_" + thumbnailFile.getOriginalFilename();
                File savedFile = new File(uploadDir, savedFileName);
                thumbnailFile.transferTo(savedFile);

                classDTO.setThumbnail(savedFileName); // 파일명 저장
            }

            int classNumber = classService.insertClass(classDTO);
            map.put("classNumber", classNumber);
            map.put("thumbnail", savedFileName); // 응답에서도 파일명으로 반환
            map.put("code", 1);
            map.put("msg", "강의 등록 성공");

        } catch (Exception e) {
            e.printStackTrace();
            map.put("code", 2);
            map.put("msg", "강의 등록 실패");
        }

        return map;
    }

    // 썸네일 제공
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

    @PostMapping("/update")
    public Map<String, Object> classUpdate(
            @RequestHeader("Authorization") String token,
            @RequestPart("params") String params,
            @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnailFile) {

        Map<String, Object> map = new HashMap<>();

        try {
            token = token != null ? token.replace("Bearer ", "") : null;
            int userGrade = tokenProvider.getRoleFromToken(token);

            System.out.println("사용자 등급: " + userGrade);
            System.out.println(params);

            if (userGrade != 1) {
                map.put("code", 3);
                map.put("msg", "강사만 강의를 수정할 수 있습니다.");
                return map;
            }

            // JSON 데이터를 DTO로 변환
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, String> paramsMap = objectMapper.readValue(params, new TypeReference<Map<String, String>>() {
            });

            System.out.println(paramsMap.get("title"));
            System.out.println(paramsMap.get("description"));
            System.out.println(paramsMap.get("classNumber"));

            ClassDTO dto = new ClassDTO();
            dto.setClassNumber(Integer.parseInt(paramsMap.get("classNumber")));
            dto.setTitle(paramsMap.get("title"));
            dto.setDescription(paramsMap.get("description"));
            dto.setCategory(paramsMap.get("category"));

            // 썸네일 변경 처리
            if (thumbnailFile != null && !thumbnailFile.isEmpty()) {
                String uploadDir = "C:\\classThumbnails";
                File dir = new File(uploadDir);
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                String fileName = UUID.randomUUID() + "_" + thumbnailFile.getOriginalFilename();
                String filePath = uploadDir + File.separator + fileName;

                System.out.println("새 썸네일 저장 경로: " + filePath);

                File file = new File(filePath);
                thumbnailFile.transferTo(file);

                dto.setThumbnail(fileName);
            }

            // 강의 정보 업데이트
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

        // 강의 삭제
/* 
    @DeleteMapping("/board/{bno}")
	public Map<String, Object> boardDelete(@PathVariable int bno, @RequestHeader("Authorization") String token) {
		Map<String, Object> map = new HashMap<>();
		token = token != null ? token.replace("Bearer ", "") : null;
		if (token != null && tokenProvider.getUserIDFromToken(token).equals(boardService.selectBoard(bno).getId())) {
			// 첨부파일 삭제
			// 1. 파일 목록 받아옴
			List<BoardFileDTO> fileList = boardService.getBoardFileList(bno);
			// 2. 파일 삭제
			fileList.forEach(file -> {
				File f = new File(file.getFpath());
				f.delete();
			});
			// 만약 board, board_file 테이블이 외래키로 cascade 제약조건이 설정되어있지 않다면, 직접 board_file 테이블의
			// 데이터를 삭제해야함.
			boardService.deleteBoard(bno);
			map.put("code", 1);
			map.put("msg", "해당 게시글 삭제를 완료하였습니다.");
		} else {
			map.put("code", 2);
			map.put("msg", "게시글 삭제를 실패하였습니다.");
		}
		return map;
	}
 */

}
