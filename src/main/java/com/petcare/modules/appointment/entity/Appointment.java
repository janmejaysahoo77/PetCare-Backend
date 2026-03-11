package com.petcare.modules.appointment.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Appointment {
    private String id;
    private String petId;
    private String ownerId; // User UID
    private String vetId; // User UID of target vet
    private LocalDateTime scheduledAt;
    private AppointmentStatus status; // PENDING, CONFIRMED, COMPLETED, CANCELLED
    private AppointmentType type; // IN_PERSON, TELECONSULT
    private String notes;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    public enum AppointmentStatus {
        PENDING, CONFIRMED, COMPLETED, CANCELLED
    }

    public enum AppointmentType {
        IN_PERSON, TELECONSULT
    }
}
