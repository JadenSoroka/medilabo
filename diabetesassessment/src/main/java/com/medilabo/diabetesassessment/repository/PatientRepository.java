package com.medilabo.diabetesassessment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.medilabo.diabetesassessment.domain.Patient;

public interface PatientRepository extends JpaRepository<Patient, Long> {

}
