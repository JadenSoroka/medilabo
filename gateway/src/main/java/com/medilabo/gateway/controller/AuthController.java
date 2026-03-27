package com.medilabo.gateway.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.medilabo.gateway.domain.LoginRequest;
import com.medilabo.gateway.domain.LoginResponse;
import com.medilabo.gateway.security.JwtUtil;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtUtil jwtUtil;
    private final String validUsername;
    private final String validPassword;

    public AuthController(
            JwtUtil jwtUtil,
            @Value("${app.auth.username}") String validUsername,
            @Value("${app.auth.password}") String validPassword) {
        this.jwtUtil = jwtUtil;
        this.validUsername = validUsername;
        this.validPassword = validPassword;
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<LoginResponse>> login(@RequestBody LoginRequest request) {
        if (validUsername.equals(request.username()) && validPassword.equals(request.password())) {
            String token = jwtUtil.generateToken(request.username());
            return Mono.just(ResponseEntity.ok(new LoginResponse(token)));
        }
        return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }
}
