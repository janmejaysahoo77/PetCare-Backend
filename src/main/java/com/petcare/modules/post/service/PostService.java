package com.petcare.modules.post.service;

import com.petcare.common.ResourceNotFoundException;
import com.petcare.modules.post.dto.AdoptionRequest;
import com.petcare.modules.post.dto.AdoptionResponse;
import com.petcare.modules.post.dto.LostPetRequest;
import com.petcare.modules.post.dto.LostPetResponse;
import com.petcare.modules.post.entity.AdoptionListing;
import com.petcare.modules.post.entity.AdoptionListing.AdoptionStatus;
import com.petcare.modules.post.entity.LostPetReport;
import com.petcare.modules.post.entity.LostPetReport.ReportStatus;
import com.petcare.modules.post.repository.AdoptionRepository;
import com.petcare.modules.post.repository.LostPetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final AdoptionRepository adoptionRepository;
    private final LostPetRepository lostPetRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    // ── Adoption Listings ───────────────────────────────────

    @Transactional
    public AdoptionResponse createAdoptionListing(String shelterId, AdoptionRequest request) {
        AdoptionListing listing = AdoptionListing.builder()
                .petId(request.getPetId())
                .shelterId(shelterId)
                .description(request.getDescription())
                .adoptionFee(request.getAdoptionFee())
                .status(AdoptionStatus.AVAILABLE)
                .build();

        listing = adoptionRepository.save(listing);
        log.info("Adoption listing {} created by shelter {}", listing.getId(), shelterId);
        return toDto(listing);
    }

    @Transactional(readOnly = true)
    public List<AdoptionResponse> getAvailableAdoptions() {
        return adoptionRepository.findByStatusOrderByCreatedAtDesc(AdoptionStatus.AVAILABLE)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional
    public AdoptionResponse updateAdoptionStatus(UUID id, AdoptionStatus newStatus) {
        AdoptionListing listing = adoptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AdoptionListing", "id", id));
        listing.setStatus(newStatus);
        return toDto(adoptionRepository.save(listing));
    }

    // ── Lost Pet Reports ────────────────────────────────────

    @Transactional
    public LostPetResponse reportLostPet(String ownerId, LostPetRequest request) {
        LostPetReport report = LostPetReport.builder()
                .petId(request.getPetId())
                .ownerId(ownerId)
                .lastSeenDate(request.getLastSeenDate())
                .lastSeenLocation(request.getLastSeenLocation())
                .description(request.getDescription())
                .rewardAmount(request.getRewardAmount())
                .status(ReportStatus.LOST)
                .build();

        report = lostPetRepository.save(report);

        // Async Event triggering Notifications nearby (Radius search via AI/Geo
        // service)
        String eventPayload = String.format("{\"reportId\":\"%s\",\"petId\":\"%s\",\"location\":\"%s\"}",
                report.getId(), report.getPetId(), report.getLastSeenLocation());
        kafkaTemplate.send("pet.lost", report.getId().toString(), eventPayload);

        log.info("Lost pet report {} created by owner {}", report.getId(), ownerId);
        return toDto(report);
    }

    @Transactional(readOnly = true)
    public List<LostPetResponse> getActiveLostPets() {
        return lostPetRepository.findByStatusOrderByCreatedAtDesc(ReportStatus.LOST)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional
    public LostPetResponse resolveLostPet(UUID id, String ownerId) {
        LostPetReport report = lostPetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("LostPetReport", "id", id));

        report.setStatus(ReportStatus.FOUND);
        report = lostPetRepository.save(report);

        kafkaTemplate.send("pet.found", report.getId().toString(),
                String.format("{\"reportId\":\"%s\",\"petId\":\"%s\"}", id, report.getPetId()));

        return toDto(report);
    }

    // ── Manual Mappers ──────────────────────────────────────

    private AdoptionResponse toDto(AdoptionListing entity) {
        if (entity == null)
            return null;
        return AdoptionResponse.builder()
                .id(entity.getId())
                .petId(entity.getPetId())
                .shelterId(entity.getShelterId())
                .description(entity.getDescription())
                .adoptionFee(entity.getAdoptionFee())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    private LostPetResponse toDto(LostPetReport entity) {
        if (entity == null)
            return null;
        return LostPetResponse.builder()
                .id(entity.getId())
                .petId(entity.getPetId())
                .ownerId(entity.getOwnerId())
                .lastSeenDate(entity.getLastSeenDate())
                .lastSeenLocation(entity.getLastSeenLocation())
                .description(entity.getDescription())
                .rewardAmount(entity.getRewardAmount())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
