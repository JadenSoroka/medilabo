package com.medilabo.frontend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
    public String patientPage(@RequestParam(required = false) String fullName, Model model) {
        if (fullName != null && !fullName.isBlank()) {
            model.addAttribute("patient", frontendService.getPatientInfo(fullName));
        }        
        return "patient";
    }    
    
}
