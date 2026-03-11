package com.medilabo.diabetesassessment.domain;

import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Document(collection = "physician_notes")
public class Note {
    @Id
    String noteId;

    @NotNull
    Long patId;

    @NotBlank
    String patient;

    @NotBlank
    String note;
}