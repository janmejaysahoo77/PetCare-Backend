package com.petcare.modules.pet.controller;

import com.petcare.common.ApiResponse;
import com.petcare.modules.pet.dto.PetRequest;
import com.petcare.modules.pet.dto.PetResponse;
import com.petcare.modules.pet.dto.VaccinationRequest;
import com.petcare.modules.pet.dto.VaccinationResponse;
import com.petcare.modules.pet.service.PetService;
import com.petcare.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pets")
@RequiredArgsConstructor
@Tag(name = "Pets", description = "Endpoints for managing pets and their health records")
public class PetController {

    private final PetService petService;

    @PostMapping
    @Operation(summary = "Register a new pet", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<PetResponse>> createPet(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody PetRequest request) {

        PetResponse response = petService.createPet(principal.getUid(), request);
        return ResponseEntity.ok(ApiResponse.success("Pet registered successfully", response));
    }

    @GetMapping
    @Operation(summary = "Get all pets owned by the current user", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<List<PetResponse>>> getMyPets(
            @AuthenticationPrincipal UserPrincipal principal) {

        List<PetResponse> pets = petService.getMyPets(principal.getUid());
        return ResponseEntity.ok(ApiResponse.success(pets));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get detailed profile of a specific pet", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<PetResponse>> getPet(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success(petService.getPet(id)));
    }

    @PostMapping("/{id}/vaccinations")
    @Operation(summary = "Add a vaccination record for a pet (VET role recommended)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<VaccinationResponse>> addVaccination(
            @PathVariable String id,
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody VaccinationRequest request) {

        // For hackathon: allowing any authenticated user to add a record.
        // In production, restrict to hasAnyRole('VET', 'ADMIN') or check ownership.
        VaccinationResponse response = petService.addVaccination(id, principal.getUid(), principal.getName(), request);
        return ResponseEntity.ok(ApiResponse.success("Vaccination added successfully", response));
    }

    @GetMapping("/{id}/vaccinations")
    @Operation(summary = "Get all vaccination records for a specific pet", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<List<VaccinationResponse>>> getVaccinations(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success(petService.getPetVaccinations(id)));
    }
}
