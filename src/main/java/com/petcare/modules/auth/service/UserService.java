package com.petcare.modules.auth.service;

import com.petcare.common.ResourceNotFoundException;
import com.petcare.modules.auth.dto.RegisterRequest;
import com.petcare.modules.auth.dto.UserResponse;
import com.petcare.modules.auth.entity.User;
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

    @Transactional
    public UserResponse registerUser(UserPrincipal principal, RegisterRequest request) {
        if (userRepository.existsById(principal.getUid())) {
            throw new IllegalArgumentException("User already registered");
        }

        User user = User.builder()
                .uid(principal.getUid())
                .email(request.getEmail() != null ? request.getEmail() : principal.getEmail())
                .displayName(request.getDisplayName() != null ? request.getDisplayName() : principal.getName())
                .phoneNumber(request.getPhoneNumber())
                .fcmToken(request.getFcmToken())
                // Default to PET_OWNER if not specified via claims yet
                .role("ROLE_PET_OWNER")
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
                .createdAt(user.getCreatedAt())
                .build();
    }
}
