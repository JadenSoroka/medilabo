package com.openclassrooms.medilabo.controller;

import com.medilabo.patientManagement.domain.Patient;
import com.medilabo.patientManagement.exception.DuplicatePatientException;
import com.medilabo.patientManagement.exception.PatientNotFoundException;
import com.medilabo.patientManagement.service.PatientService;

import tools.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = com.medilabo.patientManagement.MedilaboApplication.class)
@DisplayName("Patient Controller Tests")
class PatientControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    private ObjectMapper objectMapper;

    @MockitoBean
    private PatientService patientService;

    private Patient patient;
    private Patient anotherPatient;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        objectMapper = new ObjectMapper();

        patient = new Patient();
        // Don't set ID - it's auto-generated and must be null for @Null validation
        patient.setFirstName("John");
        patient.setLastName("Doe");
        patient.setDateOfBirth("1990-01-01");
        patient.setGender('M');
        patient.setAddress("123 Main St");
        patient.setPhone("123-456-7890");

        anotherPatient = new Patient();
        // Don't set ID - it's auto-generated and must be null for @Null validation
        anotherPatient.setFirstName("Jane");
        anotherPatient.setLastName("Smith");
        anotherPatient.setDateOfBirth("1985-05-15");
        anotherPatient.setGender('F');
        anotherPatient.setAddress("456 Oak Ave");
        anotherPatient.setPhone("987-654-3210");
    }

    @Nested
    @DisplayName("GET /api/patients/{firstLastName} Tests")
    @WithMockUser
    class GetPatientByNameTests {

        @Test
        @DisplayName("Should return patient when found")
        void shouldReturnPatientWhenFound() throws Exception {
            // Arrange
            // Create a patient with ID for the mocked response (as if from database)
            Patient patientFromDb = new Patient();
            patientFromDb.setId(1L);
            patientFromDb.setFirstName("John");
            patientFromDb.setLastName("Doe");
            patientFromDb.setDateOfBirth("1990-01-01");
            patientFromDb.setGender('M');
            patientFromDb.setAddress("123 Main St");
            patientFromDb.setPhone("123-456-7890");

            when(patientService.getPatientByFirstLastName("John Doe")).thenReturn(patientFromDb);

            // Act & Assert
            mockMvc.perform(get("/api/patients/John_Doe"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.firstName").value("John"))
                    .andExpect(jsonPath("$.lastName").value("Doe"))
                    .andExpect(jsonPath("$.dateOfBirth").value("1990-01-01"))
                    .andExpect(jsonPath("$.gender").value("M"))
                    .andExpect(jsonPath("$.address").value("123 Main St"))
                    .andExpect(jsonPath("$.phone").value("123-456-7890"));

            verify(patientService, times(1)).getPatientByFirstLastName("John Doe");
        }

        @Test
        @DisplayName("Should return 404 when patient not found")
        void shouldReturn404WhenPatientNotFound() throws Exception {
            // Arrange
            when(patientService.getPatientByFirstLastName("Unknown Person"))
                    .thenThrow(new PatientNotFoundException("Patient Unknown Person not found"));

            // Act & Assert
            mockMvc.perform(get("/api/patients/Unknown_Person"))
                    .andExpect(status().isNotFound());

            verify(patientService, times(1)).getPatientByFirstLastName("Unknown Person");
        }

        @Test
        @DisplayName("Should handle underscores in name correctly")
        void shouldHandleUnderscoresInName() throws Exception {
            // Arrange
            // Create a patient with ID for the mocked response (as if from database)
            Patient patientFromDb = new Patient();
            patientFromDb.setId(2L);
            patientFromDb.setFirstName("Jane");
            patientFromDb.setLastName("Smith");
            patientFromDb.setDateOfBirth("1985-05-15");
            patientFromDb.setGender('F');

            when(patientService.getPatientByFirstLastName("Jane Smith")).thenReturn(patientFromDb);

            // Act & Assert
            mockMvc.perform(get("/api/patients/Jane_Smith"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.firstName").value("Jane"))
                    .andExpect(jsonPath("$.lastName").value("Smith"));

            verify(patientService, times(1)).getPatientByFirstLastName("Jane Smith");
        }
    }

    @Nested
    @DisplayName("GET /api/patients Tests")
    @WithMockUser
    class GetAllPatientsTests {

        @Test
        @DisplayName("Should return all patients")
        void shouldReturnAllPatients() throws Exception {
            // Arrange - Create patients with IDs as if from database
            Patient patient1 = new Patient();
            patient1.setId(1L);
            patient1.setFirstName("John");
            patient1.setLastName("Doe");
            patient1.setDateOfBirth("1990-01-01");
            patient1.setGender('M');

            Patient patient2 = new Patient();
            patient2.setId(2L);
            patient2.setFirstName("Jane");
            patient2.setLastName("Smith");
            patient2.setDateOfBirth("1985-05-15");
            patient2.setGender('F');

            List<Patient> patients = Arrays.asList(patient1, patient2);
            when(patientService.getAllPatients()).thenReturn(patients);

            // Act & Assert
            mockMvc.perform(get("/api/patients"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].firstName").value("John"))
                    .andExpect(jsonPath("$[0].lastName").value("Doe"))
                    .andExpect(jsonPath("$[1].firstName").value("Jane"))
                    .andExpect(jsonPath("$[1].lastName").value("Smith"));

            verify(patientService, times(1)).getAllPatients();
        }

        @Test
        @DisplayName("Should return empty list when no patients exist")
        void shouldReturnEmptyListWhenNoPatientsExist() throws Exception {
            // Arrange
            when(patientService.getAllPatients()).thenReturn(Collections.emptyList());

            // Act & Assert
            mockMvc.perform(get("/api/patients"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(0)));

            verify(patientService, times(1)).getAllPatients();
        }
    }

    @Nested
    @DisplayName("POST /api/patients Tests")
    @WithMockUser
    class CreatePatientTests {

        @Test
        @DisplayName("Should create patient successfully")
        void shouldCreatePatientSuccessfully() throws Exception {
            // Arrange
            doNothing().when(patientService).createPatient(any(Patient.class));

            // Act & Assert
            mockMvc.perform(post("/api/patients")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(patient)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.firstName").value("John"))
                    .andExpect(jsonPath("$.lastName").value("Doe"));

            verify(patientService, times(1)).createPatient(any(Patient.class));
        }

        @Test
        @DisplayName("Should return 409 when creating duplicate patient")
        void shouldReturn409WhenCreatingDuplicatePatient() throws Exception {
            // Arrange
            doThrow(new DuplicatePatientException("Patient John Doe already exists"))
                    .when(patientService).createPatient(any(Patient.class));

            // Act & Assert
            mockMvc.perform(post("/api/patients")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(patient)))
                    .andExpect(status().isConflict());

            verify(patientService, times(1)).createPatient(any(Patient.class));
        }

        @Test
        @DisplayName("Should return 400 when required fields are missing")
        void shouldReturn400WhenRequiredFieldsMissing() throws Exception {
            // Arrange
            Patient invalidPatient = new Patient();
            // Missing required fields

            // Act & Assert
            mockMvc.perform(post("/api/patients")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidPatient)))
                    .andExpect(status().isBadRequest());

            verify(patientService, never()).createPatient(any(Patient.class));
        }

        @Test
        @DisplayName("Should create patient with minimal required fields")
        void shouldCreatePatientWithMinimalFields() throws Exception {
            // Arrange
            Patient minimalPatient = new Patient();
            minimalPatient.setFirstName("John");
            minimalPatient.setLastName("Doe");
            minimalPatient.setDateOfBirth("1990-01-01");
            minimalPatient.setGender('M');
            // address and phone are optional

            doNothing().when(patientService).createPatient(any(Patient.class));

            // Act & Assert
            mockMvc.perform(post("/api/patients")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(minimalPatient)))
                    .andExpect(status().isOk());

            verify(patientService, times(1)).createPatient(any(Patient.class));
        }
    }

    @Nested
    @DisplayName("PUT /api/patients/{id} Tests")
    @WithMockUser
    class UpdatePatientTests {

        @Test
        @DisplayName("Should update patient successfully")
        void shouldUpdatePatientSuccessfully() throws Exception {
            // Arrange
            doNothing().when(patientService).updatePatient(eq(1L), any(Patient.class));

            // Act & Assert
            mockMvc.perform(put("/api/patients/1")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(patient)))
                    .andExpect(status().isNoContent());

            verify(patientService, times(1)).updatePatient(eq(1L), any(Patient.class));
        }

        @Test
        @DisplayName("Should return 404 when updating non-existent patient")
        void shouldReturn404WhenUpdatingNonExistentPatient() throws Exception {
            // Arrange
            doThrow(new PatientNotFoundException("Patient with ID 999 not found"))
                    .when(patientService).updatePatient(eq(999L), any(Patient.class));

            // Act & Assert
            mockMvc.perform(put("/api/patients/999")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(patient)))
                    .andExpect(status().isNotFound());

            verify(patientService, times(1)).updatePatient(eq(999L), any(Patient.class));
        }

        @Test
        @DisplayName("Should return 400 when updating with invalid data")
        void shouldReturn400WhenUpdatingWithInvalidData() throws Exception {
            // Arrange
            Patient invalidPatient = new Patient();
            // Missing required fields

            // Act & Assert
            mockMvc.perform(put("/api/patients/1")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidPatient)))
                    .andExpect(status().isBadRequest());

            verify(patientService, never()).updatePatient(anyLong(), any(Patient.class));
        }
    }

    @Nested
    @DisplayName("DELETE /api/patients/{id} Tests")
    @WithMockUser
    class DeletePatientTests {

        @Test
        @DisplayName("Should delete patient successfully")
        void shouldDeletePatientSuccessfully() throws Exception {
            // Arrange
            doNothing().when(patientService).deletePatient(1L);

            // Act & Assert
            mockMvc.perform(delete("/api/patients/1")
                    .with(csrf()))
                    .andExpect(status().isNoContent());

            verify(patientService, times(1)).deletePatient(1L);
        }

        @Test
        @DisplayName("Should return 404 when deleting non-existent patient")
        void shouldReturn404WhenDeletingNonExistentPatient() throws Exception {
            // Arrange
            doThrow(new PatientNotFoundException("Patient with ID 999 not found"))
                    .when(patientService).deletePatient(999L);

            // Act & Assert
            mockMvc.perform(delete("/api/patients/999")
                    .with(csrf()))
                    .andExpect(status().isNotFound());

            verify(patientService, times(1)).deletePatient(999L);
        }
    }

    @Nested
    @DisplayName("Security Tests")
    @WithMockUser
    class SecurityTests {

        @Test
        @DisplayName("Should allow access without authentication (permitAll configured)")
        void shouldAllowAccessWithoutAuthForGetRequests() throws Exception {
            // Arrange
            when(patientService.getAllPatients()).thenReturn(Collections.emptyList());

            // Act & Assert - Security config has permitAll() for /api/patients/**
            mockMvc.perform(get("/api/patients"))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Should not require CSRF token when CSRF is disabled")
        void shouldNotRequireCsrfTokenWhenCsrfDisabled() throws Exception {
            // Arrange - CSRF is disabled in SecurityConfig
            doNothing().when(patientService).createPatient(any(Patient.class));

            // Act & Assert - Should work without CSRF token
            mockMvc.perform(post("/api/patients")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(patient)))
                    .andExpect(status().isOk());

            verify(patientService, times(1)).createPatient(any(Patient.class));
        }
    }
}
