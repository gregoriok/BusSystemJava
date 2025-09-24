package com.example.BusSystem.controller;

import com.example.BusSystem.config.security.TokenResponseDto;
import com.example.BusSystem.domain.User.AuthenticationData;
import com.example.BusSystem.domain.User.User;
import com.example.BusSystem.service.TokenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/login")
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;
    @PostMapping
    public ResponseEntity<?> login (@RequestBody @Valid AuthenticationData data){
        var authenticationToken = new UsernamePasswordAuthenticationToken(data.email(),data.password());
        var authentication = authenticationManager.authenticate(authenticationToken);
        var JWTtoken = tokenService.generateToken((User) authentication.getPrincipal());
        return ResponseEntity.ok(new TokenResponseDto(JWTtoken));
    }


}
