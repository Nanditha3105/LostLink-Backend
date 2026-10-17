package com.lostlink.backend.dto;

import com.lostlink.backend.entity.ClaimStatus;
import jakarta.validation.constraints.NotNull;

public record ClaimDecisionRequest(@NotNull ClaimStatus status) {
}