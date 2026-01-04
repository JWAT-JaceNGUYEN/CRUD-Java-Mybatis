package com.jwat.API_Mybatis.service.impl;


import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jwat.API_Mybatis.mapper.LoginMapper;
import com.jwat.API_Mybatis.model.request.RefreshTokenRequest;
import com.jwat.API_Mybatis.service.RefreshTokenService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final LoginMapper loginMapper;

    @Transactional
    @Override
    public void storeRefreshToken(RefreshTokenRequest request) {
        request.setTokenId(UUID.randomUUID().toString());
        loginMapper.insertRefreshToken(request);
    }

}
