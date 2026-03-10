package com.petcare.modules.post.repository;

import com.petcare.modules.post.entity.AdoptionListing;
import com.petcare.modules.post.entity.AdoptionListing.AdoptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AdoptionRepository extends JpaRepository<AdoptionListing, UUID> {
    List<AdoptionListing> findByStatusOrderByCreatedAtDesc(AdoptionStatus status);

    List<AdoptionListing> findByShelterIdOrderByCreatedAtDesc(String shelterId);
}
