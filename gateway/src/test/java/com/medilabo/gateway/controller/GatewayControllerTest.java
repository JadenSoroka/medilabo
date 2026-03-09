package com.medilabo.gateway.controller;

import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

class GatewayControllerTest {

    @Test
    void fallbackShouldReturnFriendlyMessage() {
        GatewayController controller = new GatewayController();

        StepVerifier.create(controller.fallback())
                .expectNext("Sorry, that page is down right now. Please try again later.")
                .verifyComplete();
    }
}
