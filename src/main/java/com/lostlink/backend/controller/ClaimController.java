package com.lostlink.backend.controller;

import com.lostlink.backend.dto.ClaimDecisionRequest;
import com.lostlink.backend.dto.ClaimRequest;
import com.lostlink.backend.dto.ClaimResponse;
import com.lostlink.backend.service.ClaimService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claims")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class ClaimController {

    private final ClaimService claimService;

    public ClaimController(ClaimService claimService) {
        this.claimService = claimService;
    }

    @PostMapping("/items/{itemId}")
    public ResponseEntity<ClaimResponse> create(
            @PathVariable Long itemId,
            @Valid @RequestBody ClaimRequest request,
            Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(claimService.create(itemId, request, authentication.getName()));
    }

    @GetMapping("/mine")
    public List<ClaimResponse> mine(Authentication authentication) {
        return claimService.findMyClaims(authentication.getName());
    }

    @GetMapping("/for-reports")
    public List<ClaimResponse> forReports(Authentication authentication) {
        return claimService.findClaimsForMyReports(authentication.getName());
    }

    @PatchMapping("/{claimId}/decision")
    public ClaimResponse decide(
            @PathVariable Long claimId,
            @Valid @RequestBody ClaimDecisionRequest request,
            Authentication authentication) {
        return claimService.decide(claimId, request.status(), authentication.getName());
    }

    @PostMapping("/{claimId}/confirm-return")
    public ClaimResponse confirmReturn(
            @PathVariable Long claimId,
            Authentication authentication) {
        return claimService.confirmReturn(claimId, authentication.getName());
    }
}