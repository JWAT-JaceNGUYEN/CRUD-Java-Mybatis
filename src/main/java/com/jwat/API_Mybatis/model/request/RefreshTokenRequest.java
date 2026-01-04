package com.jwat.API_Mybatis.model.request;

import java.time.OffsetDateTime;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RefreshTokenRequest {

    private String tokenId;
    @NotBlank(message = "User ID must not be blank")
    private String userId;
    @NotBlank(message = "Refresh token hash must not be blank")
    private String refreshToken;

    private OffsetDateTime expiryDate;

}
