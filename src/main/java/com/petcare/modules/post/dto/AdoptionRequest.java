package com.petcare.modules.post.dto;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class AdoptionRequest {
    private String petId;

    private String description;

    @Min(value = 0, message = "Adoption fee must be 0 or greater")
    private Double adoptionFee;
}
