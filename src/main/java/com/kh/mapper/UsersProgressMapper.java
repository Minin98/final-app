package com.kh.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UsersProgressMapper {

    int checkProgress(@Param("uno") String uno, @Param("classNumber") int classNumber);

    int applicationInsert(@Param("uno") String uno, @Param("classNumber") int classNumber);

    int cancelProgress(@Param("uno") String uno, @Param("classNumber") int classNumber);
}
