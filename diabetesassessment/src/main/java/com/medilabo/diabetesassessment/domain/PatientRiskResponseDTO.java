package com.medilabo.diabetesassessment.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PatientRiskResponseDTO(
    @NotNull
    Long patId,

    @NotBlank
    String expectedRisks
) {}
