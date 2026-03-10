package com.petcare.modules.appointment.dto;

import com.petcare.modules.appointment.entity.Appointment.AppointmentStatus;
import com.petcare.modules.appointment.entity.Appointment.AppointmentType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class AppointmentResponse {
    private UUID id;
    private UUID petId;
    private String ownerId;
    private String vetId;
    private LocalDateTime scheduledAt;
    private AppointmentStatus status;
    private AppointmentType type;
    private String notes;
    private LocalDateTime createdAt;
}
