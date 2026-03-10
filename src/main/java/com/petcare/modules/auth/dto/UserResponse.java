package com.petcare.modules.auth.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserResponse {
    private String uid;
    private String email;
    private String displayName;
    private String role;
    private String phoneNumber;
    private LocalDateTime createdAt;
}
