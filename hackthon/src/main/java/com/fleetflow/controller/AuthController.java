package com.fleetflow.controller;

import com.fleetflow.dto.user.ForgotPasswordRequest;
import com.fleetflow.dto.user.ForgotPasswordResponse;
import com.fleetflow.dto.user.LoginRequest;
import com.fleetflow.dto.user.RegisterUserRequest;
import com.fleetflow.dto.user.ResetPasswordRequest;
import com.fleetflow.dto.user.UserResponse;
import com.fleetflow.security.TokenBlacklistService;
import com.fleetflow.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final TokenBlacklistService blacklistService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(
            @Valid @RequestBody RegisterUserRequest request){
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(
            @RequestBody LoginRequest request){
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(
            @RequestHeader(value = "Authorization", required = false) String authHeader){

        if (authHeader != null && authHeader.startsWith("Bearer ") && authHeader.length() > 7) {
            String token = authHeader.substring(7);
            blacklistService.blacklist(token);
        }
        return ResponseEntity.ok("Logged out successfully");
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ForgotPasswordResponse> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {
        return ResponseEntity.ok(authService.forgotPassword(request));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {
        return ResponseEntity.ok(authService.resetPassword(request));
    }
}