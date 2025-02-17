package com.kh.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import com.kh.dto.ClassDTO;
import com.kh.service.ClassService;
import com.kh.token.JwtTokenProvider;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class MainController {
    private ClassService classService;
    private JwtTokenProvider tokenProvider;

    public MainController(ClassService classService, JwtTokenProvider tokenProvider) {
        this.classService = classService;
        this.tokenProvider = tokenProvider;
    }

    @GetMapping("/")
    public Map<String, Object> main() {
        // VIEW를 사용하여 최신 강의 조회
        List<ClassDTO> latestClasses = classService.selectLatestClasses();

        // 데이터를 담을 Map 생성
        Map<String, Object> response = new HashMap<>();
        response.put("latestClasses", latestClasses);

        return response;
    }

    @GetMapping("/classList")
    public List<ClassDTO> getClassList() {
        return classService.getAllClasses();
    }
    
}