package com.petcare.modules.auth.service;

import com.petcare.common.ResourceNotFoundException;
import com.petcare.modules.auth.dto.RegisterRequest;
import com.petcare.modules.auth.dto.UserResponse;
import com.petcare.modules.auth.entity.User;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Set;
import com.petcare.modules.auth.repository.UserRepository;
import com.petcare.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private static final Set<String> ALLOWED_ROLES = Set.of(
            "ROLE_PET_OWNER",
            "ROLE_PET_SHOP_OWNER",
            "ROLE_PET_SHELTER",
            "ROLE_VET",
            "ROLE_ADMIN"
    );

    @Transactional
    public UserResponse registerUser(UserPrincipal principal, RegisterRequest request) {
        if (userRepository.existsById(principal.getUid())) {
            throw new IllegalArgumentException("User already registered");
        }

        String role = request.getRole();
        if (role == null || role.isBlank()) {
            role = "ROLE_PET_OWNER";
        } else {
            if (!ALLOWED_ROLES.contains(role)) {
                throw new IllegalArgumentException("Invalid role provided");
            }
        }

        User user = User.builder()
                .uid(principal.getUid())
                .email(request.getEmail() != null ? request.getEmail() : principal.getEmail())
                .displayName(request.getDisplayName() != null ? request.getDisplayName() : principal.getName())
                .phoneNumber(request.getPhoneNumber())
                .fcmToken(request.getFcmToken())
                .role(role)
                .build();

        user = userRepository.save(user);
        log.info("New user registered: {} ({})", user.getUid(), user.getEmail());
        return toDto(user);
    }

    @Transactional(readOnly = true)
    public UserResponse getCurrentUser(UserPrincipal principal) {
        User user = userRepository.findById(principal.getUid())
                .orElseThrow(() -> new ResourceNotFoundException("User", "uid", principal.getUid()));
        return toDto(user);
    }

    @Transactional
    public void updateFcmToken(String uid, String fcmToken) {
        User user = userRepository.findById(uid).orElseThrow();
        user.setFcmToken(fcmToken);
        userRepository.save(user);
    }

    // Manual Object Mapper (Replacing MapStruct due to Java 24 incompatibility)
    private UserResponse toDto(User user) {
        if (user == null)
            return null;
        return UserResponse.builder()
                .uid(user.getUid())
                .email(user.getEmail())
                .displayName(user.getDisplayName())
                .role(user.getRole())
                .phoneNumber(user.getPhoneNumber())
                .createdAt(toLocalDateTime(user.getCreatedAt()))
                .build();
    }

    private LocalDateTime toLocalDateTime(Date date) {
        if (date == null) return null;
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
}
