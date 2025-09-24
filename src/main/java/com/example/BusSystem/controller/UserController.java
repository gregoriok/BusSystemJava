package com.example.BusSystem.controller;

import com.example.BusSystem.domain.Bus.BusDataInsert;
import com.example.BusSystem.domain.Bus.BusResponseDto;
import com.example.BusSystem.domain.User.UserDataInsert;
import com.example.BusSystem.domain.User.UserResponseDto;
import com.example.BusSystem.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody @Valid UserDataInsert data){
        var user = userService.create(data);
        return ResponseEntity.status(HttpStatus.CREATED).body(new UserResponseDto(user));
    }
}
