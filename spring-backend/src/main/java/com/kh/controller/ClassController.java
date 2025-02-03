package com.kh.controller;

import com.kh.service.ClassService;

import io.jsonwebtoken.lang.Collections;

import com.kh.dto.ClassDTO;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
 
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ClassController {

    private final ClassService classService;

    public ClassController(ClassService classService) { 
        this.classService = classService;
    }

    @GetMapping("/class/list")
    public List<ClassDTO> selectClassList(
            @RequestParam(required = false, defaultValue = "전체") String category,
            @RequestParam(required = false, defaultValue = "최신순") String sort) {
        return classService.selectClassList(category, sort);
    }

    @GetMapping("/class/search")
    public ResponseEntity<List<ClassDTO>> searchClasses(
            @RequestParam(required = false) String searchKeyword,
            @RequestParam(required = false, defaultValue = "전체") String category,
            @RequestParam(required = false, defaultValue = "최신순") String sort) {

                try {
                    List<ClassDTO> classList = classService.searchClasses(searchKeyword, category, sort);
                    return ResponseEntity.ok(classList);
                } catch (Exception e) {
                    e.printStackTrace(); 
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
                }
        
        // List<ClassDTO> classList = classService.searchClasses(searchKeyword, category, sort);
        // return ResponseEntity.ok(classList);
    }
}