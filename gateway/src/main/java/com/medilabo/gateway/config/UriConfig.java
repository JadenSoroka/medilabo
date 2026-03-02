package com.medilabo.gateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties
@EnableConfigurationProperties(UriConfig.class)
public class UriConfig {
    private final String backend = "http://localhost:9090";
    private final String frontend = "http://localhost:3000";

    public String getBackend() {
        return backend;
    }

    public String getFrontend() {
        return frontend;
    }
}
