package com.medilabo.physiciannotes.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

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
