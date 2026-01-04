package com.jwat.API_Mybatis.util;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Service;

import com.jwat.API_Mybatis.constants.JwtConstants;
import com.jwat.API_Mybatis.model.response.UserLoginResponse;
import com.nimbusds.jose.util.Base64;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class SecurityUtil {

    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;
    private final JwtConstants jwtConstants;

    // tạo access token khi đăng nhập
    public String createAccessToken(UserLoginResponse.UserInfo user) {

        Instant now = Instant.now();
        Instant expiry = now.plus(
                jwtConstants.getAccessExpirationMs(),
                ChronoUnit.SECONDS);

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuedAt(now)
                .expiresAt(expiry)
                .subject(user.getUserId())
                .claim("token_type", "ACCESS")
                .claim("username", user.getUsername())

                .build();

        JwsHeader header = JwsHeader
                .with(jwtConstants.JWT_ALGORITHM)
                .build();

        return jwtEncoder
                .encode(JwtEncoderParameters.from(header, claims))
                .getTokenValue();
    }

    public String createRefreshToken(String userId) {

        Instant now = Instant.now();
        Instant expiry = now.plus(
                jwtConstants.getRefreshExpirationMs(),
                ChronoUnit.SECONDS);

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuedAt(now)
                .expiresAt(expiry)
                .subject(userId)
                .claim("token_type", "REFRESH")
                .build();

        JwsHeader header = JwsHeader
                .with(jwtConstants.JWT_ALGORITHM)
                .build();

        return jwtEncoder
                .encode(JwtEncoderParameters.from(header, claims))
                .getTokenValue();
    }

    private SecretKey getSecretKey() {
        byte[] keyBytes = Base64.from(this.jwtConstants.getSecret()).decode();
        return new SecretKeySpec(keyBytes, 0, keyBytes.length,
                jwtConstants.JWT_ALGORITHM.getName());
    }

    public Jwt checkValidRefreshToken(String token) {
        try {
            Jwt jwt = jwtDecoder.decode(token);

            if (!"REFRESH".equalsIgnoreCase(jwt.getClaimAsString("token_type"))) {
                throw new JwtException("Invalid token type");
            }
            return jwt;
        } catch (Exception e) {
            System.out.println(">>> Refresh Token error: " + e.getMessage());
            throw e;
        }
    }

    public static Optional<String> getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof Jwt jwt)) {
            return Optional.empty();
        }
        return Optional.of(jwt.getSubject());
    }

    /**
     * Lấy JWT object gốc từ SecurityContext.
     * Dùng khi cần lấy các custom claim khác ngoài user/role.
     */
    public static Optional<Jwt> getCurrentJwt() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof Jwt jwt)) {
            return Optional.empty();
        }
        return Optional.of(jwt);
    }

    public String hashToken(String token) {
        return DigestUtils.sha256Hex(token);
    }

    public static Optional<String> getCurrentUserLogin() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return Optional.ofNullable(extractPrincipal(securityContext.getAuthentication()));
    }

    private static String extractPrincipal(Authentication authentication) {
        if (authentication == null) {
            return null;
        } else if (authentication.getPrincipal() instanceof UserDetails springSecurityUser) {
            return springSecurityUser.getUsername();
        } else if (authentication.getPrincipal() instanceof Jwt jwt) {
            return jwt.getSubject();
        } else if (authentication.getPrincipal() instanceof String s) {
            return s;
        }
        return null;
    }

}
