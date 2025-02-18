package com.kh.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kh.dto.RateDTO;
import com.kh.service.RateService;
import com.kh.token.JwtTokenProvider;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestPart;


@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor 
public class RateController {
    private final RateService rateService;
    private final JwtTokenProvider tokenProvider;

    @GetMapping("/class/{classNumber}/rate")
    public List<RateDTO> getRateList(@PathVariable int classNumber) {
        List<RateDTO> rateList = rateService.selectRateList(classNumber);
        return rateList;
    }
    
@PostMapping("/class/{classNumber}/rate")
    public Map<String, Object> insertRate(
        @RequestHeader("Authorization") String token,
        @RequestPart("params") String params
    ) {
        Map<String, Object> map = new HashMap<>();

        try {
            token = token != null ? token.replace("Bearer ", "") : null;
            String uno = tokenProvider.getUserNumberFromToken(token);

            ObjectMapper objectMapper = new ObjectMapper();
            RateDTO rateDTO = objectMapper.readValue(params, RateDTO.class);
            rateDTO.setUno(uno); 
            boolean result = rateService.insertRate(rateDTO);

            if (result) {
                map.put("code", 1);
                map.put("msg", "수강평이 성공적으로 등록되었습니다.");
            } else {
                map.put("code", 0);
                map.put("msg", "수강평 등록에 실패했습니다.");
            }
        } catch (Exception e) {
            map.put("code", 0);
            map.put("msg", "수강평 등록 중 오류가 발생했습니다.");
            e.printStackTrace();
        }

        return map;
    }

    @PutMapping("/class/{classNumber}/rate/{rateNumber}")
public Map<String, Object> updateRate(
        @RequestHeader("Authorization") String token,
        @PathVariable int classNumber,
        @PathVariable int rateNumber,
        @RequestBody Map<String, String> requestBody) {
    
    Map<String, Object> map = new HashMap<>();

    try {
        token = token.replace("Bearer ", "");
        String uno = tokenProvider.getUserNumberFromToken(token);
        String rateContent = requestBody.get("rateContent");

        RateDTO rateDTO = new RateDTO();
        rateDTO.setRateNumber(rateNumber);
        rateDTO.setUno(uno);
        rateDTO.setRateContent(rateContent);
        rateDTO.setRateScore(Integer.parseInt(requestBody.get("rateScore")));

        boolean result = rateService.updateRate(rateDTO);

        if (result) {
            map.put("rateList", rateService.selectRateList(classNumber)); 
        }
    } catch (Exception e) {
        e.printStackTrace();
    }

    return map;
}

@DeleteMapping("/class/{classNumber}/rate/{rateNumber}")
public Map<String, Object> deleteRate(
        @RequestHeader("Authorization") String token,
        @PathVariable int classNumber,
        @PathVariable int rateNumber) {

    Map<String, Object> map = new HashMap<>();

    try {
        token = token.replace("Bearer ", "");
        String uno = tokenProvider.getUserNumberFromToken(token);

        RateDTO rateDTO = new RateDTO();
        rateDTO.setRateNumber(rateNumber);
        rateDTO.setUno(uno);

        boolean result = rateService.deleteRate(rateDTO);

        if (result) {
            map.put("code", 1);
            map.put("msg", "수강평이 삭제되었습니다.");
            map.put("rateList", rateService.selectRateList(classNumber)); 
        } else {
            map.put("code", 0);
            map.put("msg", "수강평 삭제에 실패했습니다.");
        }
    } catch (Exception e) {
        map.put("code", 0);
        map.put("msg", "수강평 삭제 중 오류 발생");
        e.printStackTrace();
    }

    return map;
}

}