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
		return builder.routes()
				.route("backend-route", p -> p
						.path("/api/patients/**")
						.uri(backendUri))
				.route("frontend-route", p -> p
						.path("/**")
						.filters(f -> f.preserveHostHeader())
						.uri(frontendUri))
				.build();
	}
}
