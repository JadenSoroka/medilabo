package com.medilabo.frontend.controller;

import java.util.Objects;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.medilabo.frontend.domain.Patient;
import com.medilabo.frontend.service.FrontendService;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class FrontendController {
    private final FrontendService frontendService;

    public FrontendController(FrontendService frontendService) {
        this.frontendService = frontendService;
    }

    @GetMapping("/")
    public String indexPage(Model model) {
        model.addAttribute("message", "Hello, World!");
        return "index";
    }

    @GetMapping("/patient")
    public String patientPage(Model model) {
        model.addAttribute("patientList", frontendService.getAllPatients());
        return "patient";
    }

    @GetMapping("/createPatientForm")
    public String createPatientFormPage(Model model) {
        model.addAttribute("patient", new Patient(null, "", "", "", null, "", ""));
        return "createPatientForm";
    }

    @GetMapping("/updatePatientForm")
    public String updatePatientFormPage(@RequestParam Long id, Model model) {
        Patient patient = frontendService.getAllPatients()
                .stream()
                .filter(existingPatient -> Objects.equals(existingPatient.id(), id))
                .findFirst()
                .orElse(null);

        if (patient == null) {
            return "redirect:/patient";
        }

        model.addAttribute("patient", patient);
        return "updatePatientForm";
    }

    @PostMapping("/patients")
    public String createPatient(@ModelAttribute("patient") Patient patient) {
        try {
            frontendService.createPatient(patient);
        } catch (Exception e) {
            log.error("Error creating patient", e);
        }
        return "redirect:/patient";
    }

    @PutMapping("/patients/{id}")
    public String updatePatient(@PathVariable Long id, @ModelAttribute("patient") Patient patient) {
        Patient patientToUpdate = new Patient(
                null,
                patient.firstName(),
                patient.lastName(),
                patient.dateOfBirth(),
                patient.gender(),
                patient.address(),
                patient.phone());

        try {
            frontendService.updatePatient(id, patientToUpdate);
        } catch (Exception e) {
            log.error("Error updating patient", e);
        }
        return "redirect:/patient";
    }

    @PostMapping("/patients/{id}/delete")
    public String deletePatient(@PathVariable Long id) {
        try {
            frontendService.deletePatient(id);
        } catch (Exception e) {
            log.error("Error deleting patient", e);
        }
        return "redirect:/patient";
    }
}
