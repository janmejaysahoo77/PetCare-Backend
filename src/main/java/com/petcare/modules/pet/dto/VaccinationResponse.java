package com.petcare.modules.pet.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class VaccinationResponse {
    private String id;
    private String petId;
    private String vaccineName;
    private LocalDate dateGiven;
    private LocalDate nextDueDate;
    private String vetId;
    private String vetName;
    private String certificateUrl;
    private LocalDateTime createdAt;
}
