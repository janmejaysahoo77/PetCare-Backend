package com.petcare.modules.appointment.service;

import com.petcare.common.ResourceNotFoundException;
import com.petcare.modules.appointment.dto.AppointmentRequest;
import com.petcare.modules.appointment.dto.AppointmentResponse;
import com.petcare.modules.appointment.entity.Appointment;
import com.petcare.modules.appointment.entity.Appointment.AppointmentStatus;
import com.petcare.modules.appointment.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    // private final KafkaTemplate<String, Object> kafkaTemplate;

    @Transactional
    public AppointmentResponse bookAppointment(String ownerId, AppointmentRequest request) {
        Appointment appointment = Appointment.builder()
                .petId(request.getPetId())
                .ownerId(ownerId)
                .vetId(request.getVetId())
                .scheduledAt(request.getScheduledAt())
                .status(AppointmentStatus.PENDING)
                .type(request.getType())
                .notes(request.getNotes())
                .build();

        appointment = appointmentRepository.save(appointment);

        // Async Event triggering Notifications / Email
        String eventPayload = String.format("{\"appointmentId\":\"%s\",\"vetId\":\"%s\",\"ownerId\":\"%s\"}",
                appointment.getId(), appointment.getVetId(), appointment.getOwnerId());
        // kafkaTemplate.send("appointment.booked", appointment.getId(), eventPayload);

        log.info("Appointment {} booked by owner {} for vet {}", appointment.getId(), ownerId, request.getVetId());

        return toDto(appointment);
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponse> getAppointmentsForOwner(String ownerId) {
        return appointmentRepository.findByOwnerIdOrderByScheduledAtDesc(ownerId)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponse> getAppointmentsForVet(String vetId) {
        return appointmentRepository.findByVetIdOrderByScheduledAtDesc(vetId)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional
    public AppointmentResponse updateStatus(String id, AppointmentStatus newStatus, String requestingUserId) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", "id", id));

        // Note: For hackathon, basic authorization. Proper impl would verify
        // requestingUserId == vetId OR ownerId.

        AppointmentStatus oldStatus = appointment.getStatus();
        appointment.setStatus(newStatus);
        appointment = appointmentRepository.save(appointment);

        log.info("Appointment {} status changed from {} to {}", id, oldStatus, newStatus);

        if (newStatus == AppointmentStatus.CANCELLED) {
            // kafkaTemplate.send("appointment.cancelled", appointment.getId(),
            //         String.format("{\"appointmentId\":\"%s\"}", id));
        }

        return toDto(appointment);
    }

    private AppointmentResponse toDto(Appointment appt) {
        if (appt == null)
            return null;
        return AppointmentResponse.builder()
                .id(appt.getId())
                .petId(appt.getPetId())
                .ownerId(appt.getOwnerId())
                .vetId(appt.getVetId())
                .scheduledAt(appt.getScheduledAt())
                .status(appt.getStatus())
                .type(appt.getType())
                .notes(appt.getNotes())
                .createdAt(appt.getCreatedAt())
                .build();
    }
}
