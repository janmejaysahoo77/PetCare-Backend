package com.petcare.modules.post.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LostPetReport {
    private String id;
    private String petId;
    private String ownerId;
    private LocalDateTime lastSeenDate;
    private String lastSeenLocation; // E.g. GPS coordinates or address string
    private String description;
    private Double rewardAmount;
    private ReportStatus status; // LOST, FOUND, RESOLVED

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    public enum ReportStatus {
        LOST, FOUND, RESOLVED
    }
}
