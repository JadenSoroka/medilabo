package com.medilabo.frontend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.medilabo.frontend.domain.Patient;
import com.medilabo.frontend.service.FrontendService;


@RestController
@RequestMapping("/api")
public class ApiController {
    private final FrontendService patientService;

    public ApiController(FrontendService patientService) {
        this.patientService = patientService;
    }

    @GetMapping("/patient")
    public Patient getPatient(@RequestParam String fullName) {
        return patientService.getPatientInfo(fullName);
    }
    
}
