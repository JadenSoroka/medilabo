package com.medilabo.frontend.service;

import java.util.List;

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

    public List<Patient> getAllPatients() {
        return this.webClient.get()
                .uri("/patients")
                .retrieve()
                .bodyToFlux(Patient.class)
                .collectList()
                .block();
    }

    public Patient createPatient(Patient patient) {
        return this.webClient.post()
                .uri("/patients")
                .bodyValue(patient)
                .retrieve()
                .bodyToMono(Patient.class)
                .block();
    }

    public void updatePatient(Long id, Patient patient) {
        this.webClient.put()
                .uri("/patients/" + id)
                .bodyValue(patient)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    public void deletePatient(Long id) {
        this.webClient.delete()
                .uri("/patients/" + id)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

}
