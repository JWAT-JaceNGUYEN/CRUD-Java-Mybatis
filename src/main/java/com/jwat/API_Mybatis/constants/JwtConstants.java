package com.jwat.API_Mybatis.constants;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@Data
@ConfigurationProperties(prefix = "jwt")
public class JwtConstants {

    private String secret;
    private long accessExpirationMs;
    private long refreshExpirationMs;

    public static final MacAlgorithm JWT_ALGORITHM = MacAlgorithm.HS512;
}

