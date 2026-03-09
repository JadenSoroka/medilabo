package com.openclassrooms.medilabo.service;

import com.medilabo.patientManagement.domain.Patient;
import com.medilabo.patientManagement.exception.DuplicatePatientException;
import com.medilabo.patientManagement.exception.PatientNotFoundException;
import com.medilabo.patientManagement.repository.PatientRepository;
import com.medilabo.patientManagement.service.PatientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Patient Service Tests")
class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private PatientService patientService;

    private Patient patient;
    private Patient anotherPatient;

    @BeforeEach
    void setUp() {
        patient = new Patient();
        patient.setId(1L);
        patient.setFirstName("John");
        patient.setLastName("Doe");
        patient.setDateOfBirth("1990-01-01");
        patient.setGender('M');
        patient.setAddress("123 Main St");
        patient.setPhone("123-456-7890");

        anotherPatient = new Patient();
        anotherPatient.setId(2L);
        anotherPatient.setFirstName("Jane");
        anotherPatient.setLastName("Smith");
        anotherPatient.setDateOfBirth("1985-05-15");
        anotherPatient.setGender('F');
        anotherPatient.setAddress("456 Oak Ave");
        anotherPatient.setPhone("987-654-3210");
    }

    @Nested
    @DisplayName("Get All Patients Tests")
    class GetAllPatientsTests {

        @Test
        @DisplayName("Should return all patients when patients exist")
        void shouldReturnAllPatients() {
            // Arrange
            when(patientRepository.findAll()).thenReturn(Arrays.asList(patient, anotherPatient));

            // Act
            List<Patient> result = patientService.getAllPatients();

            // Assert
            assertThat(result).hasSize(2);
            assertThat(result).containsExactly(patient, anotherPatient);
            verify(patientRepository, times(1)).findAll();
        }

        @Test
        @DisplayName("Should return empty list when no patients exist")
        void shouldReturnEmptyListWhenNoPatientsExist() {
            // Arrange
            when(patientRepository.findAll()).thenReturn(Collections.emptyList());

            // Act
            List<Patient> result = patientService.getAllPatients();

            // Assert
            assertThat(result).isEmpty();
            verify(patientRepository, times(1)).findAll();
        }
    }

    @Nested
    @DisplayName("Get Patient By First and Last Name Tests")
    class GetPatientByFirstLastNameTests {

        @Test
        @DisplayName("Should return patient when found by full name")
        void shouldReturnPatientWhenFound() {
            // Arrange
            when(patientRepository.findAll()).thenReturn(Arrays.asList(patient, anotherPatient));

            // Act
            Patient result = patientService.getPatientByFirstLastName("John Doe");

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getFirstName()).isEqualTo("John");
            assertThat(result.getLastName()).isEqualTo("Doe");
            verify(patientRepository, times(1)).findAll();
        }

        @Test
        @DisplayName("Should throw PatientNotFoundException when patient not found")
        void shouldThrowExceptionWhenPatientNotFound() {
            // Arrange
            when(patientRepository.findAll()).thenReturn(Arrays.asList(patient, anotherPatient));

            // Act & Assert
            assertThatThrownBy(() -> patientService.getPatientByFirstLastName("Unknown Person"))
                    .isInstanceOf(PatientNotFoundException.class)
                    .hasMessageContaining("Patient Unknown Person not found");
            verify(patientRepository, times(1)).findAll();
        }

        @Test
        @DisplayName("Should throw PatientNotFoundException when repository is empty")
        void shouldThrowExceptionWhenRepositoryIsEmpty() {
            // Arrange
            when(patientRepository.findAll()).thenReturn(Collections.emptyList());

            // Act & Assert
            assertThatThrownBy(() -> patientService.getPatientByFirstLastName("John Doe"))
                    .isInstanceOf(PatientNotFoundException.class)
                    .hasMessageContaining("not found");
            verify(patientRepository, times(1)).findAll();
        }
    }

    @Nested
    @DisplayName("Create Patient Tests")
    class CreatePatientTests {

        @Test
        @DisplayName("Should successfully create a new patient")
        void shouldCreateNewPatient() {
            // Arrange
            when(patientRepository.findAll()).thenReturn(Collections.emptyList());
            when(patientRepository.save(any(Patient.class))).thenReturn(patient);

            // Act
            patientService.createPatient(patient);

            // Assert
            verify(patientRepository, times(1)).findAll();
            verify(patientRepository, times(1)).save(patient);
        }

        @Test
        @DisplayName("Should throw DuplicatePatientException when patient already exists")
        void shouldThrowExceptionWhenPatientExists() {
            // Arrange
            when(patientRepository.findAll()).thenReturn(Collections.singletonList(patient));

            Patient duplicatePatient = new Patient();
            duplicatePatient.setFirstName("John");
            duplicatePatient.setLastName("Doe");
            duplicatePatient.setDateOfBirth("1990-01-01");
            duplicatePatient.setGender('M');

            // Act & Assert
            assertThatThrownBy(() -> patientService.createPatient(duplicatePatient))
                    .isInstanceOf(DuplicatePatientException.class)
                    .hasMessageContaining("Patient John Doe already exists");
            verify(patientRepository, times(1)).findAll();
            verify(patientRepository, never()).save(any(Patient.class));
        }

        @Test
        @DisplayName("Should create patient when another patient with different name exists")
        void shouldCreatePatientWhenOtherPatientExists() {
            // Arrange
            when(patientRepository.findAll()).thenReturn(Collections.singletonList(patient));
            when(patientRepository.save(any(Patient.class))).thenReturn(anotherPatient);

            // Act
            patientService.createPatient(anotherPatient);

            // Assert
            verify(patientRepository, times(1)).findAll();
            verify(patientRepository, times(1)).save(anotherPatient);
        }
    }

    @Nested
    @DisplayName("Update Patient Tests")
    class UpdatePatientTests {

        @Test
        @DisplayName("Should successfully update an existing patient")
        void shouldUpdateExistingPatient() {
            // Arrange
            when(patientRepository.findAll()).thenReturn(Collections.singletonList(patient));

            Patient updatedPatient = new Patient();
            updatedPatient.setId(1L);
            updatedPatient.setFirstName("John");
            updatedPatient.setLastName("Doe");
            updatedPatient.setDateOfBirth("1990-01-01");
            updatedPatient.setGender('M');
            updatedPatient.setAddress("789 New Street");
            updatedPatient.setPhone("555-555-5555");

            // Act
            patientService.updatePatient(1L, updatedPatient);

            // Assert
            verify(patientRepository, times(1)).findAll();
            verify(patientRepository, times(1)).delete(patient);
            verify(patientRepository, times(1)).save(updatedPatient);
        }

        @Test
        @DisplayName("Should throw PatientNotFoundException when updating non-existent patient")
        void shouldThrowExceptionWhenUpdatingNonExistentPatient() {
            // Arrange
            when(patientRepository.findAll()).thenReturn(Collections.singletonList(patient));

            Patient updatedPatient = new Patient();
            updatedPatient.setId(999L);
            updatedPatient.setFirstName("Unknown");
            updatedPatient.setLastName("Person");

            // Act & Assert
            assertThatThrownBy(() -> patientService.updatePatient(999L, updatedPatient))
                    .isInstanceOf(PatientNotFoundException.class)
                    .hasMessageContaining("Patient with ID 999 not found");
            verify(patientRepository, times(1)).findAll();
            verify(patientRepository, never()).delete(any(Patient.class));
            verify(patientRepository, never()).save(any(Patient.class));
        }

        @Test
        @DisplayName("Should throw PatientNotFoundException when repository is empty")
        void shouldThrowExceptionWhenRepositoryIsEmptyDuringUpdate() {
            // Arrange
            when(patientRepository.findAll()).thenReturn(Collections.emptyList());

            // Act & Assert
            assertThatThrownBy(() -> patientService.updatePatient(1L, patient))
                    .isInstanceOf(PatientNotFoundException.class)
                    .hasMessageContaining("not found");
            verify(patientRepository, times(1)).findAll();
            verify(patientRepository, never()).delete(any(Patient.class));
            verify(patientRepository, never()).save(any(Patient.class));
        }
    }

    @Nested
    @DisplayName("Delete Patient Tests")
    class DeletePatientTests {

        @Test
        @DisplayName("Should successfully delete an existing patient")
        void shouldDeleteExistingPatient() {
            // Arrange
            when(patientRepository.findAll()).thenReturn(Collections.singletonList(patient));

            // Act
            patientService.deletePatient(1L);

            // Assert
            verify(patientRepository, times(1)).findAll();
            verify(patientRepository, times(1)).delete(patient);
        }

        @Test
        @DisplayName("Should throw PatientNotFoundException when deleting non-existent patient")
        void shouldThrowExceptionWhenDeletingNonExistentPatient() {
            // Arrange
            when(patientRepository.findAll()).thenReturn(Collections.singletonList(patient));

            // Act & Assert
            assertThatThrownBy(() -> patientService.deletePatient(999L))
                    .isInstanceOf(PatientNotFoundException.class)
                    .hasMessageContaining("Patient with ID 999 not found");
            verify(patientRepository, times(1)).findAll();
            verify(patientRepository, never()).delete(any(Patient.class));
        }

        @Test
        @DisplayName("Should throw PatientNotFoundException when repository is empty")
        void shouldThrowExceptionWhenRepositoryIsEmptyDuringDelete() {
            // Arrange
            when(patientRepository.findAll()).thenReturn(Collections.emptyList());

            // Act & Assert
            assertThatThrownBy(() -> patientService.deletePatient(1L))
                    .isInstanceOf(PatientNotFoundException.class)
                    .hasMessageContaining("not found");
            verify(patientRepository, times(1)).findAll();
            verify(patientRepository, never()).delete(any(Patient.class));
        }
    }
}
