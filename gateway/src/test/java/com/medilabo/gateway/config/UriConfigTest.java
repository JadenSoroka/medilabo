package com.medilabo.gateway.config;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UriConfigTest {

    @Test
    void shouldExposeProvidedUris() {
        UriConfig config = new UriConfig(
                "http://backend:9090",
                "http://frontend:3000",
                "http://physician-notes:9091",
                "http://diabetes-assessment:9092");

        assertThat(config.getBackend()).isEqualTo("http://backend:9090");
        assertThat(config.getFrontend()).isEqualTo("http://frontend:3000");
        assertThat(config.getPhysicianNotes()).isEqualTo("http://physician-notes:9091");
        assertThat(config.getDiabetesAssessment()).isEqualTo("http://diabetes-assessment:9092");
    }
}
