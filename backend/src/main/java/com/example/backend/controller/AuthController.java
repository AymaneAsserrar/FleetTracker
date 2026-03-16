package com.example.backend.controller;

import com.example.backend.dto.AuthDTO;
import com.example.backend.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Authentication")
public class AuthController {

    private final AuthService authService;

    @GetMapping("/me")
    @Operation(summary = "Get current user", description = "Returns fresh user info + new token based on current DB state")
    public ResponseEntity<AuthDTO.LoginResponse> me(Authentication auth) {
        return ResponseEntity.ok(authService.me(auth.getName()));
    }

    @PostMapping("/register")
    @Operation(summary = "Register", description = "Create a new driver account and receive a JWT token")
    public ResponseEntity<AuthDTO.LoginResponse> register(@RequestBody AuthDTO.RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @PostMapping("/login")
    @Operation(summary = "Login", description = "Authenticate with username and password, returns JWT token")
    public ResponseEntity<AuthDTO.LoginResponse> login(@RequestBody AuthDTO.LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
