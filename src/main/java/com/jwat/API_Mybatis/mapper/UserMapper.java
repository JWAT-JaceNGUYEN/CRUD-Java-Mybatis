package com.jwat.API_Mybatis.mapper;

import com.jwat.API_Mybatis.model.User;
import java.util.List;

public interface UserMapper {

    List<User> findAll();

    User findById(Long id);

    int insert(User user);

    void deleteById(Long id);

    int update(User user);
}
