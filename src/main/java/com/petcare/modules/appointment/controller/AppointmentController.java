package com.petcare.modules.appointment.controller;

import com.petcare.common.ApiResponse;
import com.petcare.modules.appointment.dto.AppointmentRequest;
import com.petcare.modules.appointment.dto.AppointmentResponse;
import com.petcare.modules.appointment.entity.Appointment.AppointmentStatus;
import com.petcare.modules.appointment.service.AppointmentService;
import com.petcare.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/appointments")
@RequiredArgsConstructor
@Tag(name = "Appointments", description = "Endpoints for booking and managing vet appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping
    @Operation(summary = "Book a new appointment", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<AppointmentResponse>> bookAppointment(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody AppointmentRequest request) {

        AppointmentResponse response = appointmentService.bookAppointment(principal.getUid(), request);
        return ResponseEntity.ok(ApiResponse.success("Appointment booked successfully", response));
    }

    @GetMapping("/owner")
    @Operation(summary = "Get all appointments for the current pet owner", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<List<AppointmentResponse>>> getOwnerAppointments(
            @AuthenticationPrincipal UserPrincipal principal) {

        return ResponseEntity.ok(ApiResponse.success(appointmentService.getAppointmentsForOwner(principal.getUid())));
    }

    @GetMapping("/vet")
    @Operation(summary = "Get all appointments for the current vet", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<List<AppointmentResponse>>> getVetAppointments(
            @AuthenticationPrincipal UserPrincipal principal) {

        return ResponseEntity.ok(ApiResponse.success(appointmentService.getAppointmentsForVet(principal.getUid())));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update appointment status (Confirm, Cancel, Complete)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<AppointmentResponse>> updateStatus(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody Map<String, String> payload) {

        AppointmentStatus newStatus = AppointmentStatus.valueOf(payload.get("status").toUpperCase());
        AppointmentResponse response = appointmentService.updateStatus(id, newStatus, principal.getUid());

        return ResponseEntity.ok(ApiResponse.success("Appointment status updated to " + newStatus, response));
    }
}
