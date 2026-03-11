package com.petcare.modules.pet.repository;

import com.google.cloud.firestore.Firestore;
import com.petcare.modules.pet.entity.Pet;
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
public class PetRepository {

    private final Firestore firestore;
    private static final String COLLECTION_NAME = "pets";

    public Pet save(Pet pet) {
        if (pet.getId() == null) {
            pet.setId(UUID.randomUUID().toString());
        }
        try {
            firestore.collection(COLLECTION_NAME).document(pet.getId()).set(pet).get();
            return pet;
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error saving pet", e);
            throw new RuntimeException("Failed to save pet", e);
        }
    }

    public Optional<Pet> findById(String id) {
        try {
            var docRef = firestore.collection(COLLECTION_NAME).document(id).get().get();
            if (docRef.exists()) {
                return Optional.ofNullable(docRef.toObject(Pet.class));
            }
            return Optional.empty();
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error reading pet", e);
            throw new RuntimeException("Failed to read pet", e);
        }
    }

    public boolean existsById(String id) {
        try {
            return firestore.collection(COLLECTION_NAME).document(id).get().get().exists();
        } catch (InterruptedException | ExecutionException e) {
            return false;
        }
    }

    public List<Pet> findByOwnerId(String ownerId) {
        try {
            var querySnapshot = firestore.collection(COLLECTION_NAME)
                    .whereEqualTo("ownerId", ownerId)
                    .get().get();
            return querySnapshot.getDocuments().stream()
                    .map(doc -> doc.toObject(Pet.class))
                    .collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error fetching pets for owner", e);
            throw new RuntimeException("Failed to fetch pets", e);
        }
    }
}
