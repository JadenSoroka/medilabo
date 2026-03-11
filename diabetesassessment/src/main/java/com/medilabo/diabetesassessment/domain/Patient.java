package com.medilabo.diabetesassessment.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Table(name = "patients")
@Data
public class Patient {
    @Id
    Long id;
    @NotNull
    String firstName;
    @NotNull
    String lastName;
    @NotNull
    String dateOfBirth;
    @NotNull
    Character gender;
    String address;
    String phone;
}
