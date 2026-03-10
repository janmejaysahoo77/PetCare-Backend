package com.petcare.modules.appointment.repository;

import com.petcare.modules.appointment.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {

    List<Appointment> findByOwnerIdOrderByScheduledAtDesc(String ownerId);

    List<Appointment> findByVetIdOrderByScheduledAtDesc(String vetId);

    // Optional: add find by status in the future
}
