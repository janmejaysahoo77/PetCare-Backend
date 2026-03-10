package com.petcare.modules.post.entity;

import com.petcare.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "lost_pet_reports")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LostPetReport extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(name = "pet_id", nullable = false)
    private UUID petId;

    @Column(name = "owner_id", nullable = false)
    private String ownerId;

    @Column(name = "last_seen_date", nullable = false)
    private LocalDateTime lastSeenDate;

    @Column(name = "last_seen_location", nullable = false)
    private String lastSeenLocation; // E.g. GPS coordinates or address string

    @Column(length = 1000)
    private String description;

    @Column(name = "reward_amount")
    private Double rewardAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ReportStatus status; // LOST, FOUND, RESOLVED

    public enum ReportStatus {
        LOST, FOUND, RESOLVED
    }
}
