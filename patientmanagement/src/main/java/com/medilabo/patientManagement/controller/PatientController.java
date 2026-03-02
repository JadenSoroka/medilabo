package com.medilabo.patientManagement.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.medilabo.patientManagement.domain.Patient;
import com.medilabo.patientManagement.service.PatientService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/patients")
@Slf4j
@Validated
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping("/{firstLastName}")
    public ResponseEntity<Patient> readPatient(@PathVariable String firstLastName) {
        String formattedFirstLastName = firstLastName.replace("_", " ");
        log.info("/api/patients/{} GET request for name {}", formattedFirstLastName, formattedFirstLastName);

        Patient patient = patientService.getPatientByFirstLastName(formattedFirstLastName);

        log.info("/api/patients/{} GET request for name {} successful", formattedFirstLastName, formattedFirstLastName);
        return ResponseEntity.ok(patient);
    }

    @GetMapping
    public ResponseEntity<List<Patient>> getAllPatients() {
        log.info("/api/patients GET request for all patients");

        List<Patient> patients = patientService.getAllPatients();

        log.info("/api/patients GET request for all patients successful, found {} patients", patients.size());
        return ResponseEntity.ok(patients);
    }
    

    @PostMapping
    public ResponseEntity<Patient> createPatient(@Valid @RequestBody Patient newPatient) {
        String formattedFirstLastName = newPatient.getFirstName() + " " + newPatient.getLastName();
        log.info("/api/patients POST request for name {}", formattedFirstLastName);

        patientService.createPatient(newPatient);

        log.info("/api/patients POST request for name {} successful", formattedFirstLastName);
        return ResponseEntity.ok(newPatient);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updatePatient(@PathVariable Long id, @Valid @RequestBody Patient updatedPatient) {
        log.info("/api/patients/{} PUT request for ID {}", id, id);

        patientService.updatePatient(id, updatedPatient);

        log.info("/api/patients/{} PUT request for ID {} successful", id, id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable Long id) {
        log.info("/api/patients/{} DELETE request for ID {}", id, id);

        patientService.deletePatient(id);

        log.info("/api/patients/{} DELETE request for ID {} successful", id, id);
        return ResponseEntity.noContent().build();
    }
}
