package com.petcare.modules.pet.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class PetResponse {
    private UUID id;
    private String ownerId;
    private String name;
    private String species;
    private String breed;
    private LocalDate dateOfBirth;
    private String photoUrl;
    private String blockchainHash;
    private LocalDateTime createdAt;
}
