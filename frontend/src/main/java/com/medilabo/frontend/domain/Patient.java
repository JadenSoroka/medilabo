package com.medilabo.frontend.domain;

public record Patient(
    String id,
    String firstName,
    String lastName,
    String dateOfBirth,
    Character gender,
    String address,
    String phone
) {}
