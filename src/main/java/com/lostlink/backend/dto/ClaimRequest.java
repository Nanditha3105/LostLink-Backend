package com.lostlink.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record ClaimRequest(
        @NotBlank @Size(max = 150) String foundLocation,
        @NotNull @PastOrPresent LocalDate foundDate,
        @NotBlank @Size(max = 1000) String circumstances,
        @NotBlank @Size(max = 100) String contactName,
        @NotBlank @Email @Size(max = 150) String contactEmail,
        @Size(max = 30) String contactPhone) {
}