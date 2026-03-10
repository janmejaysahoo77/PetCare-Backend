package com.petcare.modules.post.entity;

import com.petcare.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "adoption_listings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdoptionListing extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(name = "pet_id", nullable = false)
    private UUID petId;

    @Column(name = "shelter_id", nullable = false)
    private String shelterId; // ID of the Shelter User

    @Column(length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AdoptionStatus status; // AVAILABLE, PENDING, ADOPTED

    @Column(name = "adoption_fee")
    private Double adoptionFee;

    public enum AdoptionStatus {
        AVAILABLE, PENDING, ADOPTED
    }
}
