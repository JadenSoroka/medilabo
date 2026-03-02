package com.medilabo.patientManagement.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Entity
@Table(name = "patients")
@Data
@RequiredArgsConstructor
public class Patient {
    @Id
    @Null
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
