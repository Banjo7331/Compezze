package com.cmze.controller;

import com.cmze.dto.request.LoginRequest;
import com.cmze.dto.request.RefreshRequest;
import com.cmze.dto.request.RegisterRequest;
import com.cmze.dto.response.JwtAuthResponse;
import com.cmze.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<JwtAuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        JwtAuthResponse jwtAuthResponse = authService.login(loginRequest);
        return ResponseEntity.ok(jwtAuthResponse);
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest registerRequest) {
        String response = authService.register(registerRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtAuthResponse> refresh(@Valid @RequestBody RefreshRequest refreshRequest) {
        JwtAuthResponse jwtAuthResponse = authService.refresh(refreshRequest);
        return ResponseEntity.ok(jwtAuthResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(Authentication authentication) {
        authService.logout(authentication);
        return ResponseEntity.ok("User logged out successfully!");
    }
}
