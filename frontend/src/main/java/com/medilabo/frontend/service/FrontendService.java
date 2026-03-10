package com.medilabo.frontend.service;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.medilabo.frontend.domain.Note;
import com.medilabo.frontend.domain.Patient;

@Service
public class FrontendService {
    private final WebClient patientManagementWebClient;

    private final WebClient physicianNotesWebClient;

    public FrontendService(@Value("${gateway.base-url:http://localhost:9090}") String patientManagementUrl,
            @Value("${gateway.base-url:http://localhost:9091}") String physicianNotesBaseUrl) {
        this.patientManagementWebClient = WebClient.create(patientManagementUrl);
        this.physicianNotesWebClient = WebClient.create(physicianNotesBaseUrl);
    }

    public Patient getPatientInfo(String fullName) {
        String formattedFullName = fullName.replace(" ", "_");
        return this.patientManagementWebClient.get()
                .uri("/api/patients/" + formattedFullName)
                .retrieve()
                .bodyToMono(Patient.class)
                .block();
    }

    public List<Patient> getAllPatients() {
        return this.patientManagementWebClient.get()
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
        return this.physicianNotesWebClient.get()
                .uri("/api/notes/" + patientId)
                .retrieve()
                .bodyToFlux(Note.class)
                .collectList()
                .block();
    }

    public Patient createPatient(Patient patient) {
        return this.patientManagementWebClient.post()
                .uri("/api/patients")
                .bodyValue(patient)
                .retrieve()
                .bodyToMono(Patient.class)
                .block();
    }

    public void createNote(Note note) {
        this.physicianNotesWebClient.post()
                .uri("/api/notes")
                .bodyValue(note)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    public void updateNote(String noteId, Note note) {
        this.physicianNotesWebClient.put()
                .uri("/api/notes/" + noteId)
                .bodyValue(note)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    public void deleteNote(String noteId) {
        this.physicianNotesWebClient.delete()
                .uri("/api/notes/" + noteId)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    public void updatePatient(Long id, Patient patient) {
        this.patientManagementWebClient.put()
                .uri("/api/patients/" + id)
                .bodyValue(patient)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    public void deletePatient(Long id) {
        this.patientManagementWebClient.delete()
                .uri("/api/patients/" + id)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

}
