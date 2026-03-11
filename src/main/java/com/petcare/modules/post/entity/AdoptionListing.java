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
public class AdoptionListing {
    private String id;
    private String petId;
    private String shelterId; // UID
    private String description;
    private AdoptionStatus status; // AVAILABLE, PENDING, ADOPTED
    private Double adoptionFee;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    public enum AdoptionStatus {
        AVAILABLE, PENDING, ADOPTED
    }
}
