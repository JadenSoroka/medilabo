package com.medilabo.gateway.controller;

import com.medilabo.gateway.domain.LoginRequest;
import com.medilabo.gateway.domain.LoginResponse;
import com.medilabo.gateway.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private JwtUtil jwtUtil;

    private AuthController authController;

    @BeforeEach
    void setUp() {
        authController = new AuthController(jwtUtil, "testuser", "testpassword");
    }

    @Test
    void login_ValidCredentials_ReturnsOkWithToken() {
        when(jwtUtil.generateToken("testuser")).thenReturn("generated.jwt.token");

        StepVerifier.create(authController.login(new LoginRequest("testuser", "testpassword")))
                .assertNext(response -> {
                    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
                    assertThat(response.getBody()).isNotNull();
                    assertThat(response.getBody().token()).isEqualTo("generated.jwt.token");
                })
                .verifyComplete();

        verify(jwtUtil).generateToken("testuser");
    }

    @Test
    void login_InvalidUsername_ReturnsUnauthorized() {
        StepVerifier.create(authController.login(new LoginRequest("wronguser", "testpassword")))
                .assertNext(response -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED))
                .verifyComplete();

        verifyNoInteractions(jwtUtil);
    }

    @Test
    void login_InvalidPassword_ReturnsUnauthorized() {
        StepVerifier.create(authController.login(new LoginRequest("testuser", "wrongpassword")))
                .assertNext(response -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED))
                .verifyComplete();

        verifyNoInteractions(jwtUtil);
    }

    @Test
    void login_BothInvalid_ReturnsUnauthorized() {
        StepVerifier.create(authController.login(new LoginRequest("wrong", "wrong")))
                .assertNext(response -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED))
                .verifyComplete();

        verifyNoInteractions(jwtUtil);
    }

    @Test
    void login_ValidCredentials_TokenBodyContainsExpectedField() {
        when(jwtUtil.generateToken("testuser")).thenReturn("my.jwt");

        StepVerifier.create(authController.login(new LoginRequest("testuser", "testpassword")))
                .assertNext(response -> {
                    LoginResponse body = response.getBody();
                    assertThat(body).isNotNull();
                    assertThat(body.token()).isEqualTo("my.jwt");
                })
                .verifyComplete();
    }
}
