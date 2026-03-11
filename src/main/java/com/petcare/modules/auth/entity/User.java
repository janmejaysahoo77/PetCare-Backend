package com.petcare.modules.auth.entity;

import com.google.cloud.firestore.annotation.ServerTimestamp;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    private String uid; // Firebase UID
    private String email;
    private String displayName;
    private String role; // ROLE_PET_OWNER, ROLE_VET, ROLE_SHELTER, ROLE_ADMIN
    private String phoneNumber;
    private String fcmToken; // Push notification token

    @ServerTimestamp
    private Date createdAt;

    @ServerTimestamp
    private Date updatedAt;
}