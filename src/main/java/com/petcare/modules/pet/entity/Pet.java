package com.petcare.modules.pet.entity;

import com.petcare.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "pets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pet extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(name = "owner_id", nullable = false)
    private String ownerId; // References User.uid

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String species; // Dog, Cat, Bird, etc.

    private String breed;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "photo_url")
    private String photoUrl;

    @Column(name = "blockchain_hash", unique = true, updatable = false)
    private String blockchainHash; // Simulated blockchain identity
}
