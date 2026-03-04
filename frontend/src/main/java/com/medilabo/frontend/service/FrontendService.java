package com.medilabo.frontend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.medilabo.frontend.domain.Patient;

@Service
public class FrontendService {
    private final WebClient webClient;

    public FrontendService(@Value("${gateway.base-url:http://localhost:8080}") String gatewayBaseUrl) {
        this.webClient = WebClient.create(gatewayBaseUrl);
    }

    public Patient getPatientInfo(String fullName) {
        String formattedFullName = fullName.replace(" ", "_");
        return this.webClient.get()
                .uri("/api/patients/" + formattedFullName)
                .retrieve()
                .bodyToMono(Patient.class)
                .block();
    }

    public List<Patient> getAllPatients() {
        return this.webClient.get()
                .uri("/api/patients")
                .retrieve()
                .bodyToFlux(Patient.class)
                .collectList()
                .block();
    }

    public Patient createPatient(Patient patient) {
        return this.webClient.post()
                .uri("/api/patients")
                .bodyValue(patient)
                .retrieve()
                .bodyToMono(Patient.class)
                .block();
    }

    public void updatePatient(Long id, Patient patient) {
        this.webClient.put()
                .uri("/api/patients/" + id)
                .bodyValue(patient)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    public void deletePatient(Long id) {
        this.webClient.delete()
                .uri("/api/patients/" + id)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

}
