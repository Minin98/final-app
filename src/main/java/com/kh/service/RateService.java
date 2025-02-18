package com.kh.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.kh.dto.RateDTO;
import com.kh.mapper.RateMapper;

@Service
public class RateService {
    private final RateMapper mapper;

    public RateService(RateMapper mapper) {
        this.mapper = mapper;
    }

    public List<RateDTO> selectRateList(int classNumber) {
        return mapper.selectRateList(classNumber);
    }

    public boolean insertRate(RateDTO rateDTO) {
        int count = mapper.insertRate(rateDTO);
        return count > 0;
    }

    public boolean updateRate(RateDTO rateDTO) {
        int count = mapper.updateRate(rateDTO);
        return count > 0;
    }

    public boolean deleteRate(RateDTO rateDTO) {
        int count = mapper.deleteRate(rateDTO);
        return count > 0;
    }
    

}