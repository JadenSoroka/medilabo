package com.medilabo.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouteConfig {
	@Bean
	public RouteLocator myRoutes(RouteLocatorBuilder builder, UriConfig uriConfig) {
		String backendUri = uriConfig.getBackend();
		String frontendUri = uriConfig.getFrontend();
		String physicianNotesUri = uriConfig.getPhysicianNotes();
		String diabetesAssessmentUri = uriConfig.getDiabetesAssessment();
		return builder.routes()
				.route("backend-route", p -> p
						.path("/api/patients/**")
						.filters(f -> f.circuitBreaker(config -> config
								.setName("backendCircuitBreaker")
								.setFallbackUri("forward:/fallback")))
						.uri(backendUri))
				.route("physician-notes-route", p -> p
						.path("/api/notes/**")
						.filters(f -> f.circuitBreaker(config -> config
								.setName("physicianNotesCircuitBreaker")
								.setFallbackUri("forward:/fallback")))
						.uri(physicianNotesUri))
				.route("diabetes-risk-assessment-route", p -> p
						.path("/api/risk/**")
						.filters(f -> f.circuitBreaker(config -> config
								.setName("diabetesRiskAssessmentCircuitBreaker")
								.setFallbackUri("forward:/fallback")))
						.uri(diabetesAssessmentUri))
				.route("frontend-route", p -> p
						.path("/**")
						.filters(f -> f
								.preserveHostHeader()
								.circuitBreaker(config -> config
										.setName("frontendCircuitBreaker")
										.setFallbackUri("forward:/fallback")))
						.uri(frontendUri))
				.build();
	}
}
