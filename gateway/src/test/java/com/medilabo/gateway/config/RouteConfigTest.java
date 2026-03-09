package com.medilabo.gateway.config;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(properties = {
        "uri.backend=http://backend.test:9090",
        "uri.frontend=http://frontend.test:3000"
})
class RouteConfigTest {

    @Autowired
    private RouteLocator routeLocator;

    @Test
    void shouldRegisterBackendAndFrontendRoutesWithExpectedUris() {
        List<Route> routes = routeLocator.getRoutes().collectList().block();

        assertThat(routes).isNotNull();
        assertThat(routes).hasSize(2);

        Map<String, Route> routeById = routes.stream()
                .collect(Collectors.toMap(Route::getId, Function.identity()));

        assertThat(routeById).containsKeys("backend-route", "frontend-route");
        assertThat(routeById.get("backend-route").getUri().toString()).isEqualTo("http://backend.test:9090");
        assertThat(routeById.get("frontend-route").getUri().toString()).isEqualTo("http://frontend.test:3000");

        String backendPredicate = routeById.get("backend-route").getPredicate().toString();
        String frontendPredicate = routeById.get("frontend-route").getPredicate().toString();

        assertThat(backendPredicate).contains("/api/patients/**");
        assertThat(frontendPredicate).contains("/**");

        String frontendFilters = routeById.get("frontend-route").getFilters().toString();
        assertThat(frontendFilters).contains("PreserveHostHeader");
    }
}
