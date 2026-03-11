package com.petcare.modules.appointment.dto;

import com.petcare.modules.appointment.entity.Appointment.AppointmentType;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AppointmentRequest {

    @NotNull(message = "Pet ID is required")
    private String petId;

    @NotBlank(message = "Vet ID is required")
    private String vetId;

    @NotNull(message = "Scheduled time is required")
    @Future(message = "Scheduled time must be in the future")
    private LocalDateTime scheduledAt;

    @NotNull(message = "Appointment type is required")
    private AppointmentType type;

    private String notes;
}
