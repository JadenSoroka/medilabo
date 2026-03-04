package com.medilabo.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UriConfig {
    private final String backend;
    private final String frontend;

    public UriConfig(
            @Value("${uri.backend:http://localhost:9090}") String backend,
            @Value("${uri.frontend:http://localhost:3000}") String frontend) {
        this.backend = backend;
        this.frontend = frontend;
    }

    public String getBackend() {
        return backend;
    }

    public String getFrontend() {
        return frontend;
    }
}
