package com.petcare.modules.appointment.repository;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.petcare.modules.appointment.entity.Appointment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Slf4j
@Repository
@RequiredArgsConstructor
public class AppointmentRepository {

    private final Firestore firestore;
    private static final String COLLECTION_NAME = "appointments";

    public Appointment save(Appointment appointment) {
        if (appointment.getId() == null) {
            appointment.getId(); // Removed - should be set to UUID.randomUUID().toString()
            appointment.setId(UUID.randomUUID().toString());
        }
        try {
            firestore.collection(COLLECTION_NAME).document(appointment.getId()).set(appointment).get();
            return appointment;
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to save appointment", e);
        }
    }

    public Optional<Appointment> findById(String id) {
        try {
            var docRef = firestore.collection(COLLECTION_NAME).document(id).get().get();
            if (docRef.exists()) {
                return Optional.ofNullable(docRef.toObject(Appointment.class));
            }
            return Optional.empty();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to fetch appointment", e);
        }
    }

    public List<Appointment> findByOwnerIdOrderByScheduledAtDesc(String ownerId) {
        try {
            var querySnapshot = firestore.collection(COLLECTION_NAME)
                    .whereEqualTo("ownerId", ownerId)
                    .orderBy("scheduledAt", Query.Direction.DESCENDING)
                    .get().get();
            return querySnapshot.getDocuments().stream()
                    .map(doc -> doc.toObject(Appointment.class))
                    .collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to fetch appointments by owner", e);
        }
    }

    public List<Appointment> findByVetIdOrderByScheduledAtDesc(String vetId) {
        try {
            var querySnapshot = firestore.collection(COLLECTION_NAME)
                    .whereEqualTo("vetId", vetId)
                    .orderBy("scheduledAt", Query.Direction.DESCENDING)
                    .get().get();
            return querySnapshot.getDocuments().stream()
                    .map(doc -> doc.toObject(Appointment.class))
                    .collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to fetch appointments by vet", e);
        }
    }
}
