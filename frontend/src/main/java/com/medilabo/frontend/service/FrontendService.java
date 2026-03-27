package com.medilabo.frontend.service;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.WebClient;

import com.medilabo.frontend.domain.DiabetesRiskResponseDTO;
import com.medilabo.frontend.domain.Note;
import com.medilabo.frontend.domain.Patient;

import jakarta.servlet.http.HttpSession;

@Service
public class FrontendService {

    private final WebClient gatewayWebClient;

    public FrontendService(@Value("${gateway.base-url:http://localhost:8080}") String gatewayBaseUrl) {
        this.gatewayWebClient = WebClient.builder()
                .baseUrl(gatewayBaseUrl)
                .filter((request, next) -> {
                    String jwt = getJwtFromSession();
                    if (jwt != null) {
                        ClientRequest modified = ClientRequest.from(request)
                                .header("Authorization", "Bearer " + jwt)
                                .build();
                        return next.exchange(modified);
                    }
                    return next.exchange(request);
                })
                .build();
    }

    private String getJwtFromSession() {
        ServletRequestAttributes attrs =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs != null) {
            HttpSession session = attrs.getRequest().getSession(false);
            if (session != null) {
                return (String) session.getAttribute("jwt");
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public String authenticate(String username, String password) {
        Map<String, String> response = this.gatewayWebClient.post()
                .uri("/auth/login")
                .bodyValue(Map.of("username", username, "password", password))
                .retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse -> clientResponse.createException())
                .bodyToMono(Map.class)
                .block();
        return response != null ? response.get("token") : null;
    }

    public Patient getPatientInfo(String fullName) {
        String formattedFullName = fullName.replace(" ", "_");
        return this.gatewayWebClient.get()
                .uri("/api/patients/" + formattedFullName)
                .retrieve()
                .bodyToMono(Patient.class)
                .block();
    }

    public List<Patient> getAllPatients() {
        return this.gatewayWebClient.get()
                .uri("/api/patients")
                .retrieve()
                .bodyToFlux(Patient.class)
                .collectList()
                .block();
    }

    public Patient getPatientById(Long id) {
        return getAllPatients().stream()
                .filter(patient -> Objects.equals(patient.id(), id))
                .findFirst()
                .orElse(null);
    }

    public List<Note> getPatientNotes(Long patientId) {
        return this.gatewayWebClient.get()
                .uri("/api/notes/" + patientId)
                .retrieve()
                .bodyToFlux(Note.class)
                .collectList()
                .block();
    }

    public Patient createPatient(Patient patient) {
        return this.gatewayWebClient.post()
                .uri("/api/patients")
                .bodyValue(patient)
                .retrieve()
                .bodyToMono(Patient.class)
                .block();
    }

    public void createNote(Note note) {
        this.gatewayWebClient.post()
                .uri("/api/notes")
                .bodyValue(note)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    public void updateNote(String noteId, Note note) {
        this.gatewayWebClient.put()
                .uri("/api/notes/" + noteId)
                .bodyValue(note)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    public void deleteNote(String noteId) {
        this.gatewayWebClient.delete()
                .uri("/api/notes/" + noteId)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    public void updatePatient(Long id, Patient patient) {
        this.gatewayWebClient.put()
                .uri("/api/patients/" + id)
                .bodyValue(patient)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    public void deletePatient(Long id) {
        this.gatewayWebClient.delete()
                .uri("/api/patients/" + id)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    public DiabetesRiskResponseDTO getDiabetesRiskAssessment(Long patId) {
        return this.gatewayWebClient.get()
                .uri("/api/risk/" + patId)
                .retrieve()
                .bodyToMono(DiabetesRiskResponseDTO.class)
                .block();
    }

}
