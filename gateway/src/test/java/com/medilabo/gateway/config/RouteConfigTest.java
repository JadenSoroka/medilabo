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
                "uri.frontend=http://frontend.test:3000",
                "uri.physician-notes=http://physician-notes.test:9091",
                "uri.diabetes-assessment=http://diabetes-assessment.test:9092"
})
class RouteConfigTest {

        @Autowired
        private RouteLocator routeLocator;

        @Test
        void shouldRegisterBackendAndFrontendRoutesWithExpectedUris() {
                List<Route> routes = routeLocator.getRoutes().collectList().block();

                assertThat(routes).isNotNull();
                assertThat(routes).hasSize(4);

                Map<String, Route> routeById = routes.stream()
                                .collect(Collectors.toMap(Route::getId, Function.identity()));

                assertThat(routeById).containsKeys(
                                "backend-route",
                                "physician-notes-route",
                                "diabetes-risk-assessment-route",
                                "frontend-route");
                assertThat(routeById.get("backend-route").getUri().toString()).isEqualTo("http://backend.test:9090");
                assertThat(routeById.get("physician-notes-route").getUri().toString())
                                .isEqualTo("http://physician-notes.test:9091");
                assertThat(routeById.get("diabetes-risk-assessment-route").getUri().toString())
                                .isEqualTo("http://diabetes-assessment.test:9092");
                assertThat(routeById.get("frontend-route").getUri().toString()).isEqualTo("http://frontend.test:3000");

                String backendPredicate = routeById.get("backend-route").getPredicate().toString();
                String physicianNotesPredicate = routeById.get("physician-notes-route").getPredicate().toString();
                String diabetesAssessmentPredicate = routeById.get("diabetes-risk-assessment-route").getPredicate()
                                .toString();
                String frontendPredicate = routeById.get("frontend-route").getPredicate().toString();

                assertThat(backendPredicate).contains("/api/patients/**");
                assertThat(physicianNotesPredicate).contains("/api/notes/**");
                assertThat(diabetesAssessmentPredicate).contains("/api/risk/**");
                assertThat(frontendPredicate).contains("/**");

                String backendFilters = routeById.get("backend-route").getFilters().toString();
                String physicianNotesFilters = routeById.get("physician-notes-route").getFilters().toString();
                String diabetesAssessmentFilters = routeById.get("diabetes-risk-assessment-route").getFilters()
                                .toString();
                String frontendFilters = routeById.get("frontend-route").getFilters().toString();
                assertThat(backendFilters).contains("CircuitBreaker");
                assertThat(backendFilters).contains("forward:/fallback");
                assertThat(physicianNotesFilters).contains("CircuitBreaker");
                assertThat(physicianNotesFilters).contains("forward:/fallback");
                assertThat(diabetesAssessmentFilters).contains("CircuitBreaker");
                assertThat(diabetesAssessmentFilters).contains("forward:/fallback");
                assertThat(frontendFilters).contains("PreserveHostHeader");
                assertThat(frontendFilters).contains("CircuitBreaker");
                assertThat(frontendFilters).contains("forward:/fallback");
        }
}
