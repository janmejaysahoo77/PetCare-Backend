package com.petcare.modules.post.controller;

import com.petcare.common.ApiResponse;
import com.petcare.modules.post.dto.LostPetRequest;
import com.petcare.modules.post.dto.LostPetResponse;
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

@RestController
@RequestMapping("/api/v1/lost-pets")
@RequiredArgsConstructor
@Tag(name = "Lost & Found", description = "Endpoints for reporting and listing lost pets")
public class LostPetController {

    private final PostService postService;

    @PostMapping
    @Operation(summary = "Report a lost pet", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<LostPetResponse>> reportLostPet(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody LostPetRequest request) {

        LostPetResponse response = postService.reportLostPet(principal.getUid(), request);
        return ResponseEntity.ok(ApiResponse.success("Lost pet reported successfully", response));
    }

    @GetMapping
    @Operation(summary = "Get list of all active lost pet reports", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<List<LostPetResponse>>> getActiveLostPets() {
        return ResponseEntity.ok(ApiResponse.success(postService.getActiveLostPets()));
    }

    @PatchMapping("/{id}/resolve")
    @Operation(summary = "Mark a lost pet as FOUND/RESOLVED", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<LostPetResponse>> resolveLostPet(
            @PathVariable String id,
            @AuthenticationPrincipal UserPrincipal principal) {

        LostPetResponse response = postService.resolveLostPet(id, principal.getUid());
        return ResponseEntity.ok(ApiResponse.success("Pet marked as found", response));
    }
}
