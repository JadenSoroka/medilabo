package com.medilabo.gateway.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

@RestController
public class GatewayController {

    @RequestMapping("/fallback")
    public Mono<String> fallback() {
        return Mono.just("Sorry, that page is down right now. Please try again later.");
    }
}
