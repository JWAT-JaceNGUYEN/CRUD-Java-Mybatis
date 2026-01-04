package com.jwat.API_Mybatis.controller;

import com.jwat.API_Mybatis.model.User;
import com.jwat.API_Mybatis.model.request.UserCreateRequest;
import com.jwat.API_Mybatis.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.status(HttpStatus.OK).body(this.userService.getAll());
    }

    @PostMapping
    public ResponseEntity<Void> addUser(@RequestBody UserCreateRequest user) {
        this.userService.create(user);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping()
    public ResponseEntity<User> updateUser(@RequestBody User user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.update(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable long id) {
        return ResponseEntity.status(HttpStatus.OK).body(this.userService.getById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<User> deleteUserById(@PathVariable long id) {
        this.userService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}