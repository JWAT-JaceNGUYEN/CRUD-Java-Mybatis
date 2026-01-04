package com.jwat.API_Mybatis.model;

import java.time.OffsetDateTime;

import lombok.Data;

@Data
public class RefreshToken {
    private String tokenId;
    private String userId;
    private String refreshToken;
    private OffsetDateTime expiryDate;
    private Boolean revoked;
}
