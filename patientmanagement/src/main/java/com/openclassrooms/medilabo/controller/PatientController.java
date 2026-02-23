package com.openclassrooms.medilabo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.openclassrooms.medilabo.service.PatientService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.openclassrooms.medilabo.domain.Patient;
import org.springframework.web.bind.annotation.PutMapping;



@RestController
@RequestMapping("/patients")
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
        log.info("/patient GET request for name {}", formattedFirstLastName);

        Patient patient = patientService.getPatientByFirstLastName(formattedFirstLastName);

        log.info("/patient GET request for name {} successful", formattedFirstLastName);
        return ResponseEntity.ok(patient);
    }

    @PostMapping
    public ResponseEntity<Patient> createPatient(@Valid @RequestBody Patient newPatient) {
        String formattedFirstLastName = newPatient.firstName() + " " + newPatient.lastName();
        log.info("/patient POST request for name {}", formattedFirstLastName);

        patientService.createPatient(newPatient);

        log.info("/patient POST request for name {} successful", formattedFirstLastName);
        return ResponseEntity.ok(newPatient);
    }

    @PutMapping("/{firstLastName}")
    public ResponseEntity<Void> updatePatient(@PathVariable String firstLastName, @Valid @RequestBody Patient updatedPatient) {
        String formattedFirstLastName = firstLastName.replace("_", " ");
        log.info("/patient PUT request for name {}", formattedFirstLastName);

        patientService.updatePatient(formattedFirstLastName, updatedPatient);

        log.info("/patient PUT request for name {} successful", formattedFirstLastName);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{firstLastName}")
    public ResponseEntity<Void> deletePatient(@PathVariable String firstLastName) {
        String formattedFirstLastName = firstLastName.replace("_", " ");
        log.info("/patient DELETE request for name {}", formattedFirstLastName);

        patientService.deletePatient(formattedFirstLastName);

        log.info("/patient DELETE request for name {} successful", formattedFirstLastName);
        return ResponseEntity.noContent().build();
    }
}
