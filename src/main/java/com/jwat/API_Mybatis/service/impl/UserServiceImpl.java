package com.jwat.API_Mybatis.service.impl;

import com.jwat.API_Mybatis.mapper.UserMapper;
import com.jwat.API_Mybatis.service.UserService;
import com.jwat.API_Mybatis.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    @Override
    public List<User> getAll() {
        return userMapper.findAll();
    }

    @Override
    public User getById(Long id) {
        return userMapper.findById(id);
    }

    @Override
    public User create(User user) {
         userMapper.insert(user);
        return user;
    }

    @Override
    public void delete(Long id) {
        userMapper.deleteById(id);
    }

    @Override
    public User update(User user) {

        if (user.getId() == null) {
            throw new RuntimeException("User id must not be null");
        }

        int updated = userMapper.update(user);

        if (updated == 0) {
            throw new RuntimeException("Failed to update user");
        }
        return user;
    }
}
