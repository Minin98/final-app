package com.kh.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.kh.dto.ClassDTO;

@Mapper
public interface ClassMapper {

    ClassDTO selectClass(int classNumber);

    List<ClassDTO> selectLatestClasses();

    List<ClassDTO> getAllClasses();
    
    // 강의 목록 조회
    List<ClassDTO> selectClassList(
            @Param("category") String category,
            @Param("sort") String sort
    );

    List<ClassDTO> searchClasses(String searchKeyword, String category, String sort);
}
