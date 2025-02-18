package com.kh.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.kh.dto.RateDTO;

@Mapper
public interface RateMapper {

    List<RateDTO> selectRateList(int classNumber);

    int insertRate(RateDTO rateDTO);

    int updateRate(RateDTO rateDTO);

    int deleteRate(RateDTO rateDTO);
    
}