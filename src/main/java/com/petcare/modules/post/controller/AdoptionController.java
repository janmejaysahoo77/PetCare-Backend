package com.petcare.modules.post.controller;

import com.petcare.common.ApiResponse;
import com.petcare.modules.post.dto.AdoptionRequest;
import com.petcare.modules.post.dto.AdoptionResponse;
import com.petcare.modules.post.entity.AdoptionListing.AdoptionStatus;
import com.petcare.modules.post.service.PostService;
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
import java.util.Map;

@RestController
@RequestMapping("/api/v1/adoptions")
@RequiredArgsConstructor
@Tag(name = "Adoptions", description = "Endpoints for shelters to post and users to browse adoptable pets")
public class AdoptionController {

    private final PostService postService;

    @PostMapping
    @Operation(summary = "Create an adoption listing (SHELTER role recommended)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<AdoptionResponse>> createListing(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody AdoptionRequest request) {

        // For hackathon, relying on UID. In prod, check if role == ROLE_SHELTER
        AdoptionResponse response = postService.createAdoptionListing(principal.getUid(), request);
        return ResponseEntity.ok(ApiResponse.success("Adoption listing created", response));
    }

    @GetMapping
    @Operation(summary = "Browse all available pets for adoption", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<List<AdoptionResponse>>> getAvailableAdoptions() {
        return ResponseEntity.ok(ApiResponse.success(postService.getAvailableAdoptions()));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update adoption status (e.g. ADOPTED)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<AdoptionResponse>> updateStatus(
            @PathVariable String id,
            @RequestBody Map<String, String> payload) {

        AdoptionStatus status = AdoptionStatus.valueOf(payload.get("status").toUpperCase());
        return ResponseEntity.ok(ApiResponse.success(postService.updateAdoptionStatus(id, status)));
    }
}
