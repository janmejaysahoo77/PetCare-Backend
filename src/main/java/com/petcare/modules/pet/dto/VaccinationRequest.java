package com.petcare.modules.pet.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class VaccinationRequest {

    @NotBlank(message = "Vaccine name is required")
    private String vaccineName;

    @NotNull(message = "Date given is required")
    private LocalDate dateGiven;

    private LocalDate nextDueDate;

    private String vetName;

    private String certificateUrl;
}
