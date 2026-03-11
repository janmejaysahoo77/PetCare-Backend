package com.petcare.modules.auth.controller;

import com.petcare.common.ApiResponse;
import com.petcare.modules.auth.dto.RegisterRequest;
import com.petcare.modules.auth.dto.UserResponse;
import com.petcare.modules.auth.service.UserService;
import com.petcare.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for user registration and profile")
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user after Firebase auth", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<UserResponse>> register(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody RegisterRequest request) {

        UserResponse user = userService.registerUser(principal, request);
        return ResponseEntity.ok(ApiResponse.success("User registered successfully", user));
    }

    @GetMapping("/me")
    @Operation(summary = "Get current authenticated user's profile", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser(
            @AuthenticationPrincipal UserPrincipal principal) {

        UserResponse user = userService.getCurrentUser(principal);
        return ResponseEntity.ok(ApiResponse.success(user));
    }

    @PostMapping("/verify")
    @Operation(summary = "Verify token and get current authenticated user's profile", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<UserResponse>> verifyToken(
            @AuthenticationPrincipal UserPrincipal principal) {

        UserResponse user = userService.getCurrentUser(principal);
        return ResponseEntity.ok(ApiResponse.success("Token verified successfully", user));
    }

    @GetMapping("/health")
    @Operation(summary = "Public health check endpoint (no auth required)")
    public ResponseEntity<ApiResponse<String>> publicHealthCheck() {
        return ResponseEntity.ok(ApiResponse.success("PetCare+ Backend is running smoothly!"));
    }
}
