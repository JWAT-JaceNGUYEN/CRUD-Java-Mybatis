package com.jwat.API_Mybatis.model.request;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;
@Data
public class UserCreateRequest {
    
    @NotBlank(message = "Username must not be blank")
    private String username;
    @NotBlank(message = "Password must not be blank")
    private String password;

    @NotBlank(message = "Email must not be blank")
    private String email;
}