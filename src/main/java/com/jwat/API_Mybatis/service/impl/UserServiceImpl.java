package com.jwat.API_Mybatis.service.impl;

import com.jwat.API_Mybatis.mapper.UserMapper;
import com.jwat.API_Mybatis.service.UserService;
import com.jwat.API_Mybatis.model.User;
import com.jwat.API_Mybatis.model.request.UserCreateRequest;

import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<User> getAll() {
        return userMapper.findAll();
    }

    @Override
    public User getById(Long id) {
        return userMapper.findById(id);
    }

    @Override
    public void create(UserCreateRequest userCreateRequest) {
        String hashedPass = passwordEncoder.encode(userCreateRequest.getPassword());
        userCreateRequest.setPassword(hashedPass);
        userMapper.insert(userCreateRequest);
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
