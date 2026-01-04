package com.jwat.API_Mybatis.service.impl;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jwat.API_Mybatis.constants.JwtConstants;
import com.jwat.API_Mybatis.exception.NotFoundException;
import com.jwat.API_Mybatis.exception.UnauthorizedException;
import com.jwat.API_Mybatis.mapper.LoginMapper;
import com.jwat.API_Mybatis.model.RefreshToken;
import com.jwat.API_Mybatis.model.request.RefreshTokenRequest;
import com.jwat.API_Mybatis.model.request.UserLoginRequest;
import com.jwat.API_Mybatis.model.response.UserAuthResponse;
import com.jwat.API_Mybatis.model.response.UserLoginResponse;
import com.jwat.API_Mybatis.service.AuthService;
import com.jwat.API_Mybatis.service.RefreshTokenService;
import com.jwat.API_Mybatis.util.SecurityUtil;

import lombok.RequiredArgsConstructor;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

        private final SecurityUtil securityUtil;
        private final LoginMapper loginMapper;
        private final AuthenticationManagerBuilder authenticationManagerBuilder;
        private final RefreshTokenService refreshTokenService;
        private final JwtConstants jwtConstants;

        @Override
        public UserLoginResponse login(UserLoginRequest request) {

        Authentication authentication;
        try {
                authentication = authenticationManagerBuilder.getObject().authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
                );
        } catch (BadCredentialsException e) {
                throw new UnauthorizedException("Invalid username or password");
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserAuthResponse user = loginMapper.getUserAuthByUsername(request.getUsername());
        if (user == null) {
                throw new NotFoundException("User not found: " + request.getUsername());
        }

        UserLoginResponse.UserInfo userInfo = UserLoginResponse.UserInfo.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .build();

        String accessToken = securityUtil.createAccessToken(userInfo);
        String refreshToken = securityUtil.createRefreshToken(user.getUserId());

        RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest();
        refreshTokenRequest.setUserId(user.getUserId());
        refreshTokenRequest.setRefreshToken(securityUtil.hashToken(refreshToken));
        refreshTokenRequest.setExpiryDate(
                OffsetDateTime.now().plusSeconds(jwtConstants.getRefreshExpirationMs() / 1000)
        );

        refreshTokenService.storeRefreshToken(refreshTokenRequest);

        return UserLoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(userInfo)
                .build();
        }

        @Override
        @Transactional
        public UserLoginResponse getRefreshToken(String refreshToken) {

        Jwt decodedToken = securityUtil.checkValidRefreshToken(refreshToken);
        String userId = decodedToken.getSubject();

        if (userId == null || userId.isBlank()) {
                throw new UnauthorizedException("Invalid refresh token");
        }

        String refreshTokenHash = securityUtil.hashToken(refreshToken);
        RefreshToken storedToken = loginMapper.findValidToken(userId, refreshTokenHash);

        if (storedToken == null) {
                throw new UnauthorizedException("Invalid refresh token");
        }

        loginMapper.revokeToken(storedToken.getTokenId());

        UserAuthResponse user = loginMapper.getUserAuthByUserId(userId);
        if (user == null) {
                throw new NotFoundException("User not found");
        }

        UserLoginResponse.UserInfo userInfo = UserLoginResponse.UserInfo.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .build();

        String newAccessToken = securityUtil.createAccessToken(userInfo);
        String newRefreshToken = securityUtil.createRefreshToken(userId);

        RefreshTokenRequest req = new RefreshTokenRequest();
        req.setUserId(userId);
        req.setRefreshToken(securityUtil.hashToken(newRefreshToken));
        req.setExpiryDate(
                OffsetDateTime.now().plusSeconds(jwtConstants.getRefreshExpirationMs() / 1000)
        );

        refreshTokenService.storeRefreshToken(req);


        return UserLoginResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .user(userInfo)
                .build();
        }

        @Override
        public UserLoginResponse.UserInfo getAccount() {

        String userId = SecurityUtil.getCurrentUserId()
                .orElseThrow(() -> new UnauthorizedException("User not authenticated"));

        UserAuthResponse user = loginMapper.getUserAuthByUserId(userId);
        if (user == null) {
                throw new NotFoundException("User not found: " + userId);
        }

        return UserLoginResponse.UserInfo.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .build();
        }

        @Override
        @Transactional
        public void logout(String refreshToken) {

        if (refreshToken == null || refreshToken.isBlank()) {
                return;
        }

        String userId = SecurityUtil.getCurrentUserId()
                .orElseThrow(() -> new UnauthorizedException("User not authenticated"));

        String tokenHash = securityUtil.hashToken(refreshToken);
        RefreshToken token = loginMapper.findValidToken(userId, tokenHash);

        if (token != null) {
                loginMapper.revokeToken(token.getTokenId());
        }
        }

}