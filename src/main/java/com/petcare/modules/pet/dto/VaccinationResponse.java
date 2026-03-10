package com.petcare.modules.pet.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class VaccinationResponse {
    private UUID id;
    private UUID petId;
    private String vaccineName;
    private LocalDate dateGiven;
    private LocalDate nextDueDate;
    private String vetId;
    private String vetName;
    private String certificateUrl;
    private LocalDateTime createdAt;
}
