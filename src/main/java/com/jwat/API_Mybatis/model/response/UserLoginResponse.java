package com.jwat.API_Mybatis.model.response;

import java.util.List;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserLoginResponse {

    private String accessToken;
    private String refreshToken;
    private UserInfo user;

    @Data
    @Builder
    public static class UserInfo {
        private String userId;
        private String username;
    }

    @Data
    @Builder
    public static class UserGetAccount{
        private UserInfo userInfo;
    }
}

