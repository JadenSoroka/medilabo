package com.medilabo.patientManagement.controller;

import org.springframework.web.bind.annotation.RestController;

import com.medilabo.patientManagement.service.PatientManagementService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@Slf4j
public class PatientManagementController {

    private final PatientManagementService patientManagementService;

    public PatientManagementController(PatientManagementService patientManagementService) {
        this.patientManagementService = patientManagementService;
    }

    @GetMapping("/")
    public String getMethodName(@RequestParam String param) {
        return new String();
    }
    
}
