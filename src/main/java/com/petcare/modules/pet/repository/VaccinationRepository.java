package com.petcare.modules.pet.repository;

import com.petcare.modules.pet.entity.VaccinationRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface VaccinationRepository extends JpaRepository<VaccinationRecord, UUID> {
    List<VaccinationRecord> findByPetId(UUID petId);

    List<VaccinationRecord> findByPetIdOrderByDateGivenDesc(UUID petId);
}
