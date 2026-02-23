package com.openclassrooms.medilabo.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.openclassrooms.medilabo.domain.Patient;

public interface PatientRepository extends MongoRepository<Patient, String> {

}
