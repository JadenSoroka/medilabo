package com.openclassrooms.medilabo.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;

@Document("patients")
public record Patient(
    @Id
    @Null
    String id,
    @NotNull
    String firstName,
    @NotNull
    String lastName,
    @NotNull
    String dateOfBirth,
    @NotNull
    Character gender,
    String address,
    String phone
) {}
