package com.petcare.modules.pet.repository;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.petcare.modules.pet.entity.VaccinationRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Slf4j
@Repository
@RequiredArgsConstructor
public class VaccinationRepository {

    private final Firestore firestore;
    private static final String COLLECTION_NAME = "vaccinations";

    public VaccinationRecord save(VaccinationRecord record) {
        if (record.getId() == null) {
            record.setId(UUID.randomUUID().toString());
        }
        try {
            firestore.collection(COLLECTION_NAME).document(record.getId()).set(record).get();
            return record;
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to save vaccination", e);
        }
    }

    public List<VaccinationRecord> findByPetIdOrderByDateGivenDesc(String petId) {
        try {
            var querySnapshot = firestore.collection(COLLECTION_NAME)
                    .whereEqualTo("petId", petId)
                    .orderBy("dateGiven", Query.Direction.DESCENDING)
                    .get().get();
            return querySnapshot.getDocuments().stream()
                    .map(doc -> doc.toObject(VaccinationRecord.class))
                    .collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to fetch vaccinations", e);
        }
    }
}
