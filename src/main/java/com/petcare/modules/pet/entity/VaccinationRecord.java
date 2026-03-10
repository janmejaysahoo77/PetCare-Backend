package com.petcare.modules.pet.entity;

import com.petcare.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "vaccination_records")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VaccinationRecord extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(name = "pet_id", nullable = false)
    private UUID petId; // References Pet.id

    @Column(name = "vaccine_name", nullable = false)
    private String vaccineName;

    @Column(name = "date_given", nullable = false)
    private LocalDate dateGiven;

    @Column(name = "next_due_date")
    private LocalDate nextDueDate;

    @Column(name = "vet_id")
    private String vetId; // References User.uid of the Vet

    @Column(name = "vet_name")
    private String vetName; // Denormalized for display

    @Column(name = "certificate_url")
    private String certificateUrl;
}
