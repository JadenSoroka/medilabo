package com.medilabo.patientManagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.medilabo.patientManagement.domain.Patient;

public interface PatientRepository extends JpaRepository<Patient, Long> {

}
