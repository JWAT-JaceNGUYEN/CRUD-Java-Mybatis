package com.jwat.API_Mybatis.service;

import com.jwat.API_Mybatis.model.request.RefreshTokenRequest;

public interface RefreshTokenService {

    void storeRefreshToken(RefreshTokenRequest request);
}

