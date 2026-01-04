package com.jwat.API_Mybatis.mapper;

import com.jwat.API_Mybatis.model.User;
import com.jwat.API_Mybatis.model.request.UserCreateRequest;

import java.util.List;

public interface UserMapper {

    List<User> findAll();

    User findById(Long id);

    void insert(UserCreateRequest user);

    void deleteById(Long id);

    int update(User user);

    User getUserByUsername(String username);


}