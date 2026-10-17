package com.lostlink.backend.dto;

import com.lostlink.backend.entity.ClaimStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ClaimResponse(
        Long id,
        Long itemId,
        String itemTitle,
        Long finderId,
        String finderName,
        ClaimStatus status,
        String foundLocation,
        LocalDate foundDate,
        String circumstances,
        String contactName,
        String contactEmail,
        String contactPhone,
        boolean finderConfirmedReturn,
        boolean reporterConfirmedReturn,
        LocalDateTime createdAt) {
}