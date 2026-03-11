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
public class Pet {
    private String id; // Use String UUIDs for Firestore
    private String ownerId; // References User.uid
    private String name;
    private String species; // Dog, Cat, Bird, etc.
    private String breed;
    private LocalDate dateOfBirth;
    private String photoUrl;
    private String blockchainHash; // Simulated blockchain identity

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();
}
