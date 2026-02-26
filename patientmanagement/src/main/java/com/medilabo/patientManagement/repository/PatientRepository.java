package com.medilabo.patientManagement.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.medilabo.patientManagement.domain.Patient;

public interface PatientRepository extends MongoRepository<Patient, String> {

}
