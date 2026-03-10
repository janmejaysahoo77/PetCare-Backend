package com.petcare.modules.pet.service;

import com.petcare.common.ResourceNotFoundException;
import com.petcare.modules.pet.dto.PetRequest;
import com.petcare.modules.pet.dto.PetResponse;
import com.petcare.modules.pet.dto.VaccinationRequest;
import com.petcare.modules.pet.dto.VaccinationResponse;
import com.petcare.modules.pet.entity.Pet;
import com.petcare.modules.pet.entity.VaccinationRecord;
import com.petcare.modules.pet.repository.PetRepository;
import com.petcare.modules.pet.repository.VaccinationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PetService {

    private final PetRepository petRepository;
    private final VaccinationRepository vaccinationRepository;
    private final BlockchainIdentityService blockchainIdentityService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Transactional
    public PetResponse createPet(String ownerId, PetRequest request) {
        // 1. Save skeleton to generate a UUID
        Pet pet = Pet.builder()
                .ownerId(ownerId)
                .name(request.getName())
                .species(request.getSpecies())
                .breed(request.getBreed())
                .dateOfBirth(request.getDateOfBirth())
                .photoUrl(request.getPhotoUrl())
                .build();
        pet = petRepository.save(pet);

        // 2. Generate Blockchain Identity Hash
        String hash = blockchainIdentityService.generatePetIdentityHash(pet.getId(), ownerId);
        pet.setBlockchainHash(hash);
        petRepository.save(pet);

        // 3. Emit Async Action (e.g. to Gamification, Notification)
        // In a real app, you'd send a dedicated Event object. For now we just log/send
        // JSON string representation
        kafkaTemplate.send("pet.created", pet.getId().toString(),
                String.format("{\"petId\":\"%s\",\"ownerId\":\"%s\"}", pet.getId(), ownerId));
        log.info("Registered new pet ({}) with blockchain ID: {}", pet.getId(), hash);

        return toDto(pet);
    }

    @Transactional(readOnly = true)
    public List<PetResponse> getMyPets(String ownerId) {
        return petRepository.findByOwnerId(ownerId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PetResponse getPet(UUID id) {
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pet", "id", id));
        return toDto(pet);
    }

    @Transactional
    public VaccinationResponse addVaccination(UUID petId, String vetId, String vetName, VaccinationRequest request) {
        if (!petRepository.existsById(petId)) {
            throw new ResourceNotFoundException("Pet", "id", petId);
        }

        VaccinationRecord record = VaccinationRecord.builder()
                .petId(petId)
                .vaccineName(request.getVaccineName())
                .dateGiven(request.getDateGiven())
                .nextDueDate(request.getNextDueDate())
                .vetId(vetId)
                .vetName(request.getVetName() != null ? request.getVetName() : vetName)
                .certificateUrl(request.getCertificateUrl())
                .build();

        record = vaccinationRepository.save(record);
        return toDto(record);
    }

    @Transactional(readOnly = true)
    public List<VaccinationResponse> getPetVaccinations(UUID petId) {
        return vaccinationRepository.findByPetIdOrderByDateGivenDesc(petId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // ── Manual Mappers ──────────────────────────────────────

    private PetResponse toDto(Pet pet) {
        if (pet == null)
            return null;
        return PetResponse.builder()
                .id(pet.getId())
                .ownerId(pet.getOwnerId())
                .name(pet.getName())
                .species(pet.getSpecies())
                .breed(pet.getBreed())
                .dateOfBirth(pet.getDateOfBirth())
                .photoUrl(pet.getPhotoUrl())
                .blockchainHash(pet.getBlockchainHash())
                .createdAt(pet.getCreatedAt())
                .build();
    }

    private VaccinationResponse toDto(VaccinationRecord record) {
        if (record == null)
            return null;
        return VaccinationResponse.builder()
                .id(record.getId())
                .petId(record.getPetId())
                .vaccineName(record.getVaccineName())
                .dateGiven(record.getDateGiven())
                .nextDueDate(record.getNextDueDate())
                .vetId(record.getVetId())
                .vetName(record.getVetName())
                .certificateUrl(record.getCertificateUrl())
                .createdAt(record.getCreatedAt())
                .build();
    }
}
