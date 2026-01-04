package com.jwat.API_Mybatis.service;

import com.jwat.API_Mybatis.model.request.UserLoginRequest;
import com.jwat.API_Mybatis.model.response.UserLoginResponse;

public interface AuthService {

    UserLoginResponse login(UserLoginRequest request);

    UserLoginResponse getRefreshToken(String refreshToken);

    UserLoginResponse.UserInfo getAccount();

    void logout(String refreshToken);
}

