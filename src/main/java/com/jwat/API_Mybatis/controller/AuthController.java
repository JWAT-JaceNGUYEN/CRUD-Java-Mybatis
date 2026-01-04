package com.jwat.API_Mybatis.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jwat.API_Mybatis.constants.JwtConstants;
import com.jwat.API_Mybatis.model.request.UserLoginRequest;
import com.jwat.API_Mybatis.model.response.UserLoginResponse;
import com.jwat.API_Mybatis.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

        private final AuthService authService;
        private final JwtConstants jwtConstants;


        @PostMapping("/login")
        public ResponseEntity<UserLoginResponse> login(@Valid @RequestBody UserLoginRequest request) {
                UserLoginResponse res = authService.login(request);
                ResponseCookie resCookies = ResponseCookie
                                .from("refresh_token", res.getRefreshToken())
                                .httpOnly(true)
                                .secure(true)
                                .path("/")
                                .maxAge(jwtConstants.getRefreshExpirationMs())
                                .build();

                return ResponseEntity.ok()
                                .header(HttpHeaders.SET_COOKIE, resCookies.toString())
                                .body(res);
        }

        @GetMapping("/refresh")
        public ResponseEntity<UserLoginResponse> getRefreshToken(
                        @CookieValue(name = "refresh_token") String refresh_token) {
                UserLoginResponse res = this.authService.getRefreshToken(refresh_token);
                // set cookie
                ResponseCookie resCookies = ResponseCookie
                                .from("refresh_token", res.getRefreshToken())
                                .httpOnly(true)
                                .secure(true)
                                .path("/")
                                .maxAge(jwtConstants.getRefreshExpirationMs())
                                .build();

                return ResponseEntity.ok()
                                .header(HttpHeaders.SET_COOKIE, resCookies.toString())
                                .body(res);
        }

        @GetMapping("/account")
        public ResponseEntity<UserLoginResponse.UserInfo> getAccount() {
                UserLoginResponse.UserInfo userInfo = authService.getAccount();
                return ResponseEntity.ok(userInfo);
        }

        @PostMapping("/logout")
        public ResponseEntity<Void> logout(
                        @CookieValue(name = "refresh_token", required = false) String refreshToken) {

                authService.logout(refreshToken);

                ResponseCookie deleteCookie = ResponseCookie
                                .from("refresh_token", "")
                                .httpOnly(true)
                                .secure(true)
                                .path("/")
                                .maxAge(0)
                                .build();

                return ResponseEntity.ok()
                                .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
                                .body(null);
        }

}
