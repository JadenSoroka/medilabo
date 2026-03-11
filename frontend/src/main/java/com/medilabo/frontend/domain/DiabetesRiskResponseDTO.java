package com.medilabo.frontend.domain;

public record DiabetesRiskResponseDTO(
    Long patId,
    String expectedRisks
) {}
