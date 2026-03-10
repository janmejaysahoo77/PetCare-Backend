package com.petcare.modules.pet.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PetRequest {

    @NotBlank(message = "Pet name is required")
    private String name;

    @NotBlank(message = "Species is required")
    private String species;

    private String breed;

    private LocalDate dateOfBirth;

    private String photoUrl;
}
