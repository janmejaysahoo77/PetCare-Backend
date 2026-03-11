package com.petcare.modules.post.repository;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.petcare.modules.post.entity.LostPetReport;
import com.petcare.modules.post.entity.LostPetReport.ReportStatus;
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
public class LostPetRepository {

    private final Firestore firestore;
    private static final String COLLECTION_NAME = "lost_pet_reports";

    public LostPetReport save(LostPetReport report) {
        if (report.getId() == null) {
            report.setId(UUID.randomUUID().toString());
        }
        try {
            firestore.collection(COLLECTION_NAME).document(report.getId()).set(report).get();
            return report;
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to save lost pet report", e);
        }
    }

    public Optional<LostPetReport> findById(String id) {
        try {
            var docRef = firestore.collection(COLLECTION_NAME).document(id).get().get();
            if (docRef.exists()) {
                return Optional.ofNullable(docRef.toObject(LostPetReport.class));
            }
            return Optional.empty();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to fetch lost pet report", e);
        }
    }

    public List<LostPetReport> findByStatusOrderByCreatedAtDesc(ReportStatus status) {
        try {
            var querySnapshot = firestore.collection(COLLECTION_NAME)
                    .whereEqualTo("status", status.name())
                    .orderBy("createdAt", Query.Direction.DESCENDING)
                    .get().get();
            return querySnapshot.getDocuments().stream()
                    .map(doc -> doc.toObject(LostPetReport.class))
                    .collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to fetch lost pet reports by status", e);
        }
    }
}
