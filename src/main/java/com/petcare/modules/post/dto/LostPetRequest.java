package com.petcare.modules.post.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class LostPetRequest {

    @NotNull(message = "Pet ID is required")
    private UUID petId;

    @NotNull(message = "Last seen date is required")
    private LocalDateTime lastSeenDate;

    @NotBlank(message = "Last seen location is required")
    private String lastSeenLocation;

    private String description;

    private Double rewardAmount;
}
