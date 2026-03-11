package com.petcare.modules.post.dto;

import com.petcare.modules.post.entity.AdoptionListing.AdoptionStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AdoptionResponse {
    private String id;
    private String petId;
    private String shelterId;
    private String description;
    private Double adoptionFee;
    private AdoptionStatus status;
    private LocalDateTime createdAt;
}
