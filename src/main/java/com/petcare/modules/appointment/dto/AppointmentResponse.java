package com.petcare.modules.appointment.dto;

import com.petcare.modules.appointment.entity.Appointment.AppointmentStatus;
import com.petcare.modules.appointment.entity.Appointment.AppointmentType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AppointmentResponse {
    private String id;
    private String petId;
    private String ownerId;
    private String vetId;
    private LocalDateTime scheduledAt;
    private AppointmentStatus status;
    private AppointmentType type;
    private String notes;
    private LocalDateTime createdAt;
}
