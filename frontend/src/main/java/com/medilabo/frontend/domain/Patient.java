package com.medilabo.frontend.domain;

public record Patient(
    Long id,
    String firstName,
    String lastName,
    String dateOfBirth,
    Character gender,
    String address,
    String phone
) {}
