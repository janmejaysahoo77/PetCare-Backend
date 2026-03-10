package com.petcare.modules.pet.repository;

import com.petcare.modules.pet.entity.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PetRepository extends JpaRepository<Pet, UUID> {
    List<Pet> findByOwnerId(String ownerId);

    boolean existsByBlockchainHash(String hash);
}
