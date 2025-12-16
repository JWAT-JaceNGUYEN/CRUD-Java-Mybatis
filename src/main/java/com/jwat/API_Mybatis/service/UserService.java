package com.jwat.API_Mybatis.service;

import com.jwat.API_Mybatis.model.User;
import java.util.List;


public interface UserService {
    List<User> getAll();
    User getById(Long id);
    User create(User user);
    void delete(Long id);
    User update(User user);
}
