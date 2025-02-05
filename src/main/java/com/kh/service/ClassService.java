package com.kh.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kh.dto.ClassDTO;
import com.kh.mapper.ClassMapper;

@Service
public class ClassService {

    private ClassMapper mapper;

    public ClassService(ClassMapper mapper) {
        this.mapper = mapper;
    }

    public ClassDTO selectClass(int classNumber) {
        return mapper.selectClass(classNumber);
    }

    public List<ClassDTO> selectLatestClasses() {
        return mapper.selectLatestClasses();
    }

    public List<ClassDTO> getAllClasses() {
        return mapper.getAllClasses();
    }

    // 강의 목록 조회
    public List<ClassDTO> selectClassList(String category, String sort) {
        return mapper.selectClassList(category, sort);
    }

    public List<ClassDTO> searchClasses(String searchKeyword, String category, String sort) {
        return mapper.searchClasses(searchKeyword, category, sort);
    }

    public int insertClass(ClassDTO classDTO) {
        int classNumber = mapper.selectClassNo();
        classDTO.setClassNumber(classNumber);

        int count = mapper.insertClass(classDTO);

        return count > 0 ? classNumber : -1; // 성공하면 강의 번호, 실패하면 -1 반환
    }

}
