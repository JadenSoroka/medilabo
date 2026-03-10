package com.medilabo.frontend.domain;

public record Note(
    String noteId,
    Long patId,
    String patient,
    String note
) {}
