package com.petcare.modules.post.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class AdoptionRequest {

    @NotNull(message = "Pet ID is required")
    private UUID petId;

    private String description;

    @Min(value = 0, message = "Adoption fee must be 0 or greater")
    private Double adoptionFee;
}
