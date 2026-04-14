package com.medilabo.diabetesassessment.handler;

import com.medilabo.diabetesassessment.exception.PatientNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handlePatientNotFoundException_ReturnsNotFoundWithCorrectBody() {
        PatientNotFoundException ex = new PatientNotFoundException("Patient not found");

        ResponseEntity<Map<String, Object>> response = handler.handlePatientNotFoundException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).containsEntry("status", 404);
        assertThat(response.getBody()).containsEntry("error", "Patient not found");
        assertThat(response.getBody()).containsEntry("message", "Patient not found");
        assertThat(response.getBody()).containsKey("timestamp");
    }

    @Test
    void handlePatientNotFoundException_CustomMessage_IsPreservedInBody() {
        PatientNotFoundException ex = new PatientNotFoundException("No patient with id 42");

        ResponseEntity<Map<String, Object>> response = handler.handlePatientNotFoundException(ex);

        assertThat(response.getBody()).containsEntry("message", "No patient with id 42");
    }

    @Test
    void handleValidation_WithFieldErrors_ReturnsBadRequestWithFieldErrors() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(
                List.of(new FieldError("patient", "firstName", "must not be null")));

        ResponseEntity<Map<String, Object>> response = handler.handleValidation(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).containsEntry("status", 400);
        assertThat(response.getBody()).containsEntry("error", "Validation Failed");
        assertThat(response.getBody()).containsEntry("message", "Request validation failed");
        @SuppressWarnings("unchecked")
        Map<String, String> fieldErrors = (Map<String, String>) response.getBody().get("fieldErrors");
        assertThat(fieldErrors).containsEntry("firstName", "must not be null");
    }

    @Test
    void handleValidation_MultipleFieldErrors_AllIncludedInResponse() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(
                new FieldError("patient", "firstName", "must not be null"),
                new FieldError("patient", "lastName", "must not be blank")));

        ResponseEntity<Map<String, Object>> response = handler.handleValidation(ex);

        @SuppressWarnings("unchecked")
        Map<String, String> fieldErrors = (Map<String, String>) response.getBody().get("fieldErrors");
        assertThat(fieldErrors).hasSize(2);
        assertThat(fieldErrors).containsEntry("firstName", "must not be null");
        assertThat(fieldErrors).containsEntry("lastName", "must not be blank");
    }

    @Test
    void handleValidation_NoFieldErrors_ReturnsEmptyFieldErrors() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of());

        ResponseEntity<Map<String, Object>> response = handler.handleValidation(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        @SuppressWarnings("unchecked")
        Map<String, String> fieldErrors = (Map<String, String>) response.getBody().get("fieldErrors");
        assertThat(fieldErrors).isEmpty();
    }
}
