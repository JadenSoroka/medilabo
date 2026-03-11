package com.medilabo.diabetesassessment.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.medilabo.diabetesassessment.domain.PatientRiskResponseDTO;
import com.medilabo.diabetesassessment.service.DiabetesService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/api/risk")
@Slf4j
public class DiabetesController {
    private final DiabetesService diabetesService;

    public DiabetesController(DiabetesService diabetesService) {
        this.diabetesService = diabetesService;
    }

    @GetMapping("/{patId}")
    public ResponseEntity<PatientRiskResponseDTO> getDiabetesRisk(@PathVariable Long patId) {
        log.info("/api/risk GET request recieved for patient ID: {}", patId);

        String expectedRisks = diabetesService.calculateRisk(patId);
        PatientRiskResponseDTO patientRiskResponseDTO = new PatientRiskResponseDTO(patId, expectedRisks);

        log.info("/api/risk GET request successful");
        return ResponseEntity.ok(patientRiskResponseDTO);
    }
    
}
