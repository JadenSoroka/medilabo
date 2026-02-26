package com.medilabo.frontend.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.medilabo.frontend.domain.Patient;

@Service
public class FrontendService {
    private final WebClient webClient = WebClient.create("http://localhost:8080");

    public Patient getPatientInfo(String fullName) {
        String formattedFullName = fullName.replace(" ", "_");
        return this.webClient.get()
            .uri("/patients/" + formattedFullName)
            .retrieve()
            .bodyToMono(Patient.class)
            .block();
    }

}
