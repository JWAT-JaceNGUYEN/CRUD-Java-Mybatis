package com.jwat.API_Mybatis.service;

import com.jwat.API_Mybatis.model.User;
import com.jwat.API_Mybatis.model.request.UserCreateRequest;

import java.util.List;


public interface UserService {
    List<User> getAll();
    User getById(Long id);
    void create(UserCreateRequest user);
    void delete(Long id);
    User update(User user);
}
