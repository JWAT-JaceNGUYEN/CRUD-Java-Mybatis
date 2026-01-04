package com.jwat.API_Mybatis.mapper;

import java.sql.Ref;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.jwat.API_Mybatis.model.RefreshToken;
import com.jwat.API_Mybatis.model.request.RefreshTokenRequest;
import com.jwat.API_Mybatis.model.response.UserAuthResponse;
import com.jwat.API_Mybatis.model.response.UserLoginResponse;


@Mapper
public interface LoginMapper {

    UserLoginResponse.UserInfo getLoginInfoByUsername(String username);

    void insertRefreshToken(RefreshTokenRequest refreshTokenRequest);

    List<String> findRolesByUserId(String userId);

    List<String> findPermissionsByUserId(String userId);

    UserAuthResponse getUserAuthByUsername(String username);

    UserAuthResponse getUserByRefreshTokenAndUsername(String refreshToken, String username);

    RefreshToken findValidToken(
        @Param("userId") String userId,
        @Param("refreshTokenHash") String refreshTokenHash
    );

    void revokeToken(@Param("tokenId") String tokenId);

    UserAuthResponse getUserAuthByUserId(
        @Param("userId") String userId
    );

}