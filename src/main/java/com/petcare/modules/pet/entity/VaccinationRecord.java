package com.petcare.modules.pet.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VaccinationRecord {
    private String id;
    private String petId; // References Pet.id
    private String vaccineName;
    private LocalDate dateGiven;
    private LocalDate nextDueDate;
    private String vetId; // References Vet User.uid
    private String vetName;
    private String certificateUrl;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();
}
