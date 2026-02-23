package com.openclassrooms.medilabo.service;

import org.springframework.stereotype.Service;

import com.openclassrooms.medilabo.domain.Patient;
import com.openclassrooms.medilabo.exception.DuplicatePatientException;
import com.openclassrooms.medilabo.exception.PatientNotFoundException;
import com.openclassrooms.medilabo.repository.PatientRepository;

@Service
public class PatientService {

    private final PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public Patient getPatientByFirstLastName(String firstLastName) {
        System.out.println("PatientService.getPatientByFirstLastName called with name: " + firstLastName);
        return patientRepository.findAll().stream()
            .filter(patient -> (patient.firstName() + " " + patient.lastName()).equals(firstLastName))
            .findFirst()
            .orElseThrow(() -> new PatientNotFoundException("Patient " + firstLastName + " not found"));
    }

    public void createPatient(Patient newPatient) throws DuplicatePatientException {
        patientRepository.findAll().stream()
            .filter(patient -> (patient.firstName() + " " + patient.lastName()).equals(newPatient.firstName() + " " + newPatient.lastName()))
            .findFirst()
            .ifPresent(existingPatient -> {
                throw new DuplicatePatientException(
                    "Patient " + newPatient.firstName() + " " + newPatient.lastName() + " already exists"
                );
            });
        patientRepository.save(newPatient);
    }

    public void updatePatient(String formattedFirstLastName, Patient updatedPatient) {
        patientRepository.findAll().stream()
            .filter(patient -> (patient.firstName() + " " + patient.lastName()).equals(formattedFirstLastName))
            .findFirst()
            .ifPresentOrElse(existingPatient -> {
                patientRepository.delete(existingPatient);
                patientRepository.save(updatedPatient);
            }, () -> {
                throw new PatientNotFoundException("Patient " + formattedFirstLastName + " not found");
            });
    }

    public void deletePatient(String formattedFirstLastName) {
        patientRepository.findAll().stream()
            .filter(patient -> (patient.firstName() + " " + patient.lastName()).equals(formattedFirstLastName))
            .findFirst()
            .ifPresentOrElse(existingPatient -> {
                patientRepository.delete(existingPatient);
            }, () -> {
                throw new PatientNotFoundException("Patient " + formattedFirstLastName + " not found");
            });
    }

}
