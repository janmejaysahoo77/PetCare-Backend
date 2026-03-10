package com.petcare.modules.post.dto;

import com.petcare.modules.post.entity.LostPetReport.ReportStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class LostPetResponse {
    private UUID id;
    private UUID petId;
    private String ownerId;
    private LocalDateTime lastSeenDate;
    private String lastSeenLocation;
    private String description;
    private Double rewardAmount;
    private ReportStatus status;
    private LocalDateTime createdAt;
}
