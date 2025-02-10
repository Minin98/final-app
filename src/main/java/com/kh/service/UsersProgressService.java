package com.kh.service;

import org.springframework.stereotype.Service;

import com.kh.mapper.UsersProgressMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsersProgressService {

    private final UsersProgressMapper usersProgressMapper;

    public boolean checkProgress(String uno, int classNumber) {
        return usersProgressMapper.checkProgress(uno, classNumber) > 0;
    }

    public int applicationInsert(String uno, int classNumber) {
        return usersProgressMapper.applicationInsert(uno, classNumber);
    }

    public int cancelProgress(String uno, int classNumber) {
        return usersProgressMapper.cancelProgress(uno, classNumber);
    }

}
