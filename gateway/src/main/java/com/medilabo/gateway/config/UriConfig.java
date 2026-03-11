package com.medilabo.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UriConfig {
    private final String backend;
    private final String frontend;
    private final String physicianNotes;
    private final String diabetesAssessment;

    public UriConfig(
            @Value("${uri.backend:http://localhost:9090}") String backend,
            @Value("${uri.frontend:http://localhost:3000}") String frontend,
            @Value("${uri.physician-notes:http://localhost:9091}") String physicianNotes,
            @Value("${uri.diabetes-assessment:http://localhost:9092}") String diabetesAssessment) {
        this.backend = backend;
        this.frontend = frontend;
        this.physicianNotes = physicianNotes;
        this.diabetesAssessment = diabetesAssessment;
    }

    public String getBackend() {
        return backend;
    }

    public String getFrontend() {
        return frontend;
    }

    public String getPhysicianNotes() {
        return physicianNotes;
    }

    public String getDiabetesAssessment() {
        return diabetesAssessment;
    }
}
