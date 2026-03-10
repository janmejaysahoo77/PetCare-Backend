package com.petcare.modules.auth.entity;

import com.petcare.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity {

    @Id
    @Column(name = "uid", updatable = false, nullable = false)
    private String uid; // Firebase UID serves as the primary key

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "display_name", nullable = false)
    private String displayName;

    @Column(nullable = false)
    private String role; // ROLE_PET_OWNER, ROLE_VET, ROLE_SHELTER, ROLE_ADMIN

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "fcm_token")
    private String fcmToken; // For push notifications
}
