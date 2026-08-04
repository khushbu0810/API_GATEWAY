package com.example.users.controller;

import com.example.users.entity.UserDTO;
import com.example.users.entity.UserRequest;
import com.example.users.entity.UserResponse;
import com.example.users.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private Environment environment;

    @Autowired
    private UserService userService;

    @GetMapping("/status/check")
    public String status() {
        return "Working on port " + environment.getProperty("local.server.port");
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest user) {
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(user, userDTO);

        UserDTO createdUser = userService.createUser(userDTO);

        UserResponse response = new UserResponse();
        BeanUtils.copyProperties(createdUser, response);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
