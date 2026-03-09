package com.openclassrooms.medilabo.handler;

import com.medilabo.patientManagement.exception.DuplicatePatientException;
import com.medilabo.patientManagement.exception.PatientNotFoundException;
import com.medilabo.patientManagement.handler.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("Global Exception Handler Tests")
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    @DisplayName("Should handle PatientNotFoundException correctly")
    void shouldHandlePatientNotFoundException() {
        // Arrange
        String errorMessage = "Patient John Doe not found";
        PatientNotFoundException exception = new PatientNotFoundException(errorMessage);

        // Act
        ResponseEntity<Map<String, Object>> response = exceptionHandler.handlePatientNotFound(exception);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("status")).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(response.getBody().get("error")).isEqualTo("Patient Not Found");
        assertThat(response.getBody().get("message")).isEqualTo(errorMessage);
        assertThat(response.getBody().get("timestamp")).isInstanceOf(LocalDateTime.class);
    }

    @Test
    @DisplayName("Should handle DuplicatePatientException correctly")
    void shouldHandleDuplicatePatientException() {
        // Arrange
        String errorMessage = "Patient John Doe already exists";
        DuplicatePatientException exception = new DuplicatePatientException(errorMessage);

        // Act
        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleDuplicatePatientException(exception);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("status")).isEqualTo(HttpStatus.CONFLICT.value());
        assertThat(response.getBody().get("error")).isEqualTo("Duplicate Patient");
        assertThat(response.getBody().get("message")).isEqualTo(errorMessage);
        assertThat(response.getBody().get("timestamp")).isInstanceOf(LocalDateTime.class);
    }

    @Test
    @DisplayName("Should handle MethodArgumentNotValidException with single field error")
    void shouldHandleMethodArgumentNotValidExceptionWithSingleFieldError() {
        // Arrange
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("patient", "firstName", "must not be null");

        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        // Act
        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleValidation(exception);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("status")).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getBody().get("error")).isEqualTo("Validation Failed");
        assertThat(response.getBody().get("message")).isEqualTo("Request validation failed");
        assertThat(response.getBody().get("timestamp")).isInstanceOf(LocalDateTime.class);

        @SuppressWarnings("unchecked")
        Map<String, String> fieldErrors = (Map<String, String>) response.getBody().get("fieldErrors");
        assertThat(fieldErrors).containsEntry("firstName", "must not be null");
    }

    @Test
    @DisplayName("Should handle MethodArgumentNotValidException with multiple field errors")
    void shouldHandleMethodArgumentNotValidExceptionWithMultipleFieldErrors() {
        // Arrange
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError1 = new FieldError("patient", "firstName", "must not be null");
        FieldError fieldError2 = new FieldError("patient", "lastName", "must not be null");
        FieldError fieldError3 = new FieldError("patient", "dateOfBirth", "must not be null");

        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError1, fieldError2, fieldError3));

        // Act
        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleValidation(exception);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();

        @SuppressWarnings("unchecked")
        Map<String, String> fieldErrors = (Map<String, String>) response.getBody().get("fieldErrors");
        assertThat(fieldErrors).hasSize(3);
        assertThat(fieldErrors).containsEntry("firstName", "must not be null");
        assertThat(fieldErrors).containsEntry("lastName", "must not be null");
        assertThat(fieldErrors).containsEntry("dateOfBirth", "must not be null");
    }

    @Test
    @DisplayName("Should handle MethodArgumentNotValidException with empty field errors")
    void shouldHandleMethodArgumentNotValidExceptionWithEmptyFieldErrors() {
        // Arrange
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);

        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of());

        // Act
        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleValidation(exception);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();

        @SuppressWarnings("unchecked")
        Map<String, String> fieldErrors = (Map<String, String>) response.getBody().get("fieldErrors");
        assertThat(fieldErrors).isEmpty();
    }

    @Test
    @DisplayName("Should include timestamp in all error responses")
    void shouldIncludeTimestampInAllErrorResponses() {
        // Arrange
        PatientNotFoundException notFoundException = new PatientNotFoundException("Test message");
        DuplicatePatientException duplicateException = new DuplicatePatientException("Test message");

        // Act
        LocalDateTime beforeCalls = LocalDateTime.now().minusSeconds(1);
        ResponseEntity<Map<String, Object>> response1 = exceptionHandler.handlePatientNotFound(notFoundException);
        ResponseEntity<Map<String, Object>> response2 = exceptionHandler
                .handleDuplicatePatientException(duplicateException);
        LocalDateTime afterCalls = LocalDateTime.now().plusSeconds(1);

        // Assert
        LocalDateTime timestamp1 = (LocalDateTime) response1.getBody().get("timestamp");
        LocalDateTime timestamp2 = (LocalDateTime) response2.getBody().get("timestamp");

        assertThat(timestamp1).isBetween(beforeCalls, afterCalls);
        assertThat(timestamp2).isBetween(beforeCalls, afterCalls);
    }
}
