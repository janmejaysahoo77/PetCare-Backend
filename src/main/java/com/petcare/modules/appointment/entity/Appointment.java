package com.petcare.modules.appointment.entity;

import com.petcare.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "appointments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Appointment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(name = "pet_id", nullable = false)
    private UUID petId;

    @Column(name = "owner_id", nullable = false)
    private String ownerId; // User UID

    @Column(name = "vet_id", nullable = false)
    private String vetId; // User UID of target vet

    @Column(name = "scheduled_at", nullable = false)
    private LocalDateTime scheduledAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AppointmentStatus status; // PENDING, CONFIRMED, COMPLETED, CANCELLED

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AppointmentType type; // IN_PERSON, TELECONSULT

    @Column(length = 500)
    private String notes;

    public enum AppointmentStatus {
        PENDING, CONFIRMED, COMPLETED, CANCELLED
    }

    public enum AppointmentType {
        IN_PERSON, TELECONSULT
    }
}
