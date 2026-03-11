package com.petcare.modules.post.repository;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.petcare.modules.post.entity.AdoptionListing;
import com.petcare.modules.post.entity.AdoptionListing.AdoptionStatus;
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
public class AdoptionRepository {

    private final Firestore firestore;
    private static final String COLLECTION_NAME = "adoption_listings";

    public AdoptionListing save(AdoptionListing listing) {
        if (listing.getId() == null) {
            listing.setId(UUID.randomUUID().toString());
        }
        try {
            firestore.collection(COLLECTION_NAME).document(listing.getId()).set(listing).get();
            return listing;
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to save adoption listing", e);
        }
    }

    public Optional<AdoptionListing> findById(String id) {
        try {
            var docRef = firestore.collection(COLLECTION_NAME).document(id).get().get();
            if (docRef.exists()) {
                return Optional.ofNullable(docRef.toObject(AdoptionListing.class));
            }
            return Optional.empty();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to fetch adoption listing", e);
        }
    }

    public List<AdoptionListing> findByStatusOrderByCreatedAtDesc(AdoptionStatus status) {
        try {
            var querySnapshot = firestore.collection(COLLECTION_NAME)
                    .whereEqualTo("status", status.name())
                    .orderBy("createdAt", Query.Direction.DESCENDING)
                    .get().get();
            return querySnapshot.getDocuments().stream()
                    .map(doc -> doc.toObject(AdoptionListing.class))
                    .collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to fetch available adoptions", e);
        }
    }
}
