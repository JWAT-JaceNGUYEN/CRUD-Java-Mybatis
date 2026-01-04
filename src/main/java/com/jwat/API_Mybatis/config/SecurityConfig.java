package com.jwat.API_Mybatis.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jwat.API_Mybatis.constants.JwtConstants;
import com.jwat.API_Mybatis.model.request.ApiResponse;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import lombok.RequiredArgsConstructor;

import java.nio.charset.StandardCharsets;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {


        private final JwtConstants jwtConstants;

        private final CorsConfig corsConfig;

        private static final String[] PUBLIC_API = new String[] {
                        "/api/v1/auth/login",
                        "/api/v1/auth/refresh",
                        "/api/v1/users"
        };

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        // Chain 1: Public/auth endpoints → permitAll, Not enable oauth2ResourceServer
        @Bean
        @Order(1)
        public SecurityFilterChain publicChain(HttpSecurity http) throws Exception {
                http
                                .securityMatcher(PUBLIC_API)
                                .csrf(AbstractHttpConfigurer::disable)
                                .cors(cors -> cors.configurationSource(corsConfig.corsConfigurationSource()))
                                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
                return http.build();
        }

        // Chain 2: Protected endpoints → require JWT
        @Bean
        @Order(2)
        public SecurityFilterChain apiChain(
                        HttpSecurity http,
                        CustomAuthenticationEntryPoint entryPoint,
                        ObjectMapper objectMapper) throws Exception {
                http
                                .securityMatcher("/api/**") // only secure API routes
                                .csrf(AbstractHttpConfigurer::disable)
                                .cors(cors -> cors.configurationSource(corsConfig.corsConfigurationSource()))
                                .authorizeHttpRequests(auth -> auth
                                                .anyRequest().authenticated())
                                .oauth2ResourceServer(oauth2 -> oauth2
                                                .jwt(Customizer.withDefaults())
                                                .authenticationEntryPoint(entryPoint))
                                .exceptionHandling(ex -> ex.accessDeniedHandler((req, res, e) -> {
                                        res.setStatus(HttpStatus.FORBIDDEN.value());
                                        res.setContentType("application/json;charset=UTF-8");
                                        var body = new ApiResponse<>();
                                        body.setStatusCode(403);
                                        body.setMessage("Insufficient permissions");
                                        body.setResult(null);
                                        objectMapper.writeValue(res.getWriter(), body);
                                }))
                                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
                return http.build();
        }

        @Bean
        public JwtAuthenticationConverter jwtAuthenticationConverter() {
                JwtGrantedAuthoritiesConverter granted = new JwtGrantedAuthoritiesConverter();
                granted.setAuthorityPrefix(""); // keep your roles/permissions as-is
                granted.setAuthoritiesClaimName("permission");
                JwtAuthenticationConverter conv = new JwtAuthenticationConverter();
                conv.setJwtGrantedAuthoritiesConverter(granted);
                return conv;
        }

        @Bean
        public JwtDecoder jwtDecoder() {
                NimbusJwtDecoder decoder = NimbusJwtDecoder.withSecretKey(getSecretKey())
                                .macAlgorithm(jwtConstants.JWT_ALGORITHM)
                                .build();
                return token -> {
                        try {
                                return decoder.decode(token);
                        } catch (Exception e) {
                                System.out.println(">>> JWT error: " + e.getMessage());
                                throw e;
                        }
                };
        }

        @Bean
        public JwtEncoder jwtEncoder() {
                return new NimbusJwtEncoder(new ImmutableSecret<>(getSecretKey()));
        }

        private SecretKey getSecretKey() {
                byte[] keyBytes = jwtConstants.getSecret()
                                .getBytes(StandardCharsets.UTF_8);

                return new SecretKeySpec(
                                keyBytes,
                                jwtConstants.JWT_ALGORITHM.getName());
        }


        

}


