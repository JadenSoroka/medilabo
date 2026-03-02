package com.medilabo.patientManagement.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.medilabo.patientManagement.domain.Patient;
import com.medilabo.patientManagement.exception.DuplicatePatientException;
import com.medilabo.patientManagement.exception.PatientNotFoundException;
import com.medilabo.patientManagement.repository.PatientRepository;

@Service
public class PatientService {

    private final PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    // Future scenario: Paginate response
    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    // Future scenario: Build DB query to find patient by first and last name instead of filtering in memory
    public Patient getPatientByFirstLastName(String firstLastName) {
        return patientRepository.findAll().stream()
            .filter(patient -> (patient.getFirstName() + " " + patient.getLastName()).equals(firstLastName))
            .findFirst()
            .orElseThrow(() -> new PatientNotFoundException("Patient " + firstLastName + " not found"));
    }

    public void createPatient(Patient newPatient) throws DuplicatePatientException {
        patientRepository.findAll().stream()
            .filter(patient -> (patient.getFirstName() + " " + patient.getLastName()).equals(newPatient.getFirstName() + " " + newPatient.getLastName()))
            .findFirst()
            .ifPresent(existingPatient -> {
                throw new DuplicatePatientException(
                    "Patient " + newPatient.getFirstName() + " " + newPatient.getLastName() + " already exists"
                );
            });
        patientRepository.save(newPatient);
    }

    public void updatePatient(Long id, Patient updatedPatient) {
        patientRepository.findAll().stream()
            .filter(patient -> patient.getId().equals(id))
            .findFirst()
            .ifPresentOrElse(existingPatient -> {
                patientRepository.delete(existingPatient);
                patientRepository.save(updatedPatient);
            }, () -> {
                throw new PatientNotFoundException("Patient with ID " + id + " not found");
            });
    }

    public void deletePatient(Long id) {
        patientRepository.findAll().stream()
            .filter(patient -> patient.getId().equals(id))
            .findFirst()
            .ifPresentOrElse(existingPatient -> {
                patientRepository.delete(existingPatient);
            }, () -> {
                throw new PatientNotFoundException("Patient with ID " + id + " not found");
            });
    }

}
