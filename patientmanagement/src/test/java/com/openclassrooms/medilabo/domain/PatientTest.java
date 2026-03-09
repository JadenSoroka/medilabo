package com.openclassrooms.medilabo.domain;

import com.medilabo.patientManagement.domain.Patient;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Patient Entity Tests")
class PatientTest {

    private Validator validator;
    private Patient patient;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        patient = new Patient();
        patient.setFirstName("John");
        patient.setLastName("Doe");
        patient.setDateOfBirth("1990-01-01");
        patient.setGender('M');
        patient.setAddress("123 Main St");
        patient.setPhone("123-456-7890");
    }

    @Nested
    @DisplayName("Validation Tests")
    class ValidationTests {

        @Test
        @DisplayName("Should validate successfully with all required fields")
        void shouldValidateSuccessfullyWithAllRequiredFields() {
            // Act
            Set<ConstraintViolation<Patient>> violations = validator.validate(patient);

            // Assert
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should validate successfully with only required fields")
        void shouldValidateSuccessfullyWithOnlyRequiredFields() {
            // Arrange
            patient.setAddress(null);
            patient.setPhone(null);

            // Act
            Set<ConstraintViolation<Patient>> violations = validator.validate(patient);

            // Assert
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should fail validation when firstName is null")
        void shouldFailValidationWhenFirstNameIsNull() {
            // Arrange
            patient.setFirstName(null);

            // Act
            Set<ConstraintViolation<Patient>> violations = validator.validate(patient);

            // Assert
            assertThat(violations).hasSize(1);
            ConstraintViolation<Patient> violation = violations.iterator().next();
            assertThat(violation.getPropertyPath().toString()).isEqualTo("firstName");
            assertThat(violation.getMessage()).contains("must not be null");
        }

        @Test
        @DisplayName("Should fail validation when lastName is null")
        void shouldFailValidationWhenLastNameIsNull() {
            // Arrange
            patient.setLastName(null);

            // Act
            Set<ConstraintViolation<Patient>> violations = validator.validate(patient);

            // Assert
            assertThat(violations).hasSize(1);
            ConstraintViolation<Patient> violation = violations.iterator().next();
            assertThat(violation.getPropertyPath().toString()).isEqualTo("lastName");
            assertThat(violation.getMessage()).contains("must not be null");
        }

        @Test
        @DisplayName("Should fail validation when dateOfBirth is null")
        void shouldFailValidationWhenDateOfBirthIsNull() {
            // Arrange
            patient.setDateOfBirth(null);

            // Act
            Set<ConstraintViolation<Patient>> violations = validator.validate(patient);

            // Assert
            assertThat(violations).hasSize(1);
            ConstraintViolation<Patient> violation = violations.iterator().next();
            assertThat(violation.getPropertyPath().toString()).isEqualTo("dateOfBirth");
            assertThat(violation.getMessage()).contains("must not be null");
        }

        @Test
        @DisplayName("Should fail validation when gender is null")
        void shouldFailValidationWhenGenderIsNull() {
            // Arrange
            patient.setGender(null);

            // Act
            Set<ConstraintViolation<Patient>> violations = validator.validate(patient);

            // Assert
            assertThat(violations).hasSize(1);
            ConstraintViolation<Patient> violation = violations.iterator().next();
            assertThat(violation.getPropertyPath().toString()).isEqualTo("gender");
            assertThat(violation.getMessage()).contains("must not be null");
        }

        @Test
        @DisplayName("Should fail validation when multiple required fields are null")
        void shouldFailValidationWhenMultipleRequiredFieldsAreNull() {
            // Arrange
            patient.setFirstName(null);
            patient.setLastName(null);
            patient.setDateOfBirth(null);

            // Act
            Set<ConstraintViolation<Patient>> violations = validator.validate(patient);

            // Assert
            assertThat(violations).hasSize(3);
        }

        @Test
        @DisplayName("Should fail validation when id is not null for new patient")
        void shouldFailValidationWhenIdIsNotNullForNewPatient() {
            // Arrange
            patient.setId(1L);

            // Act
            Set<ConstraintViolation<Patient>> violations = validator.validate(patient);

            // Assert
            assertThat(violations).hasSize(1);
            ConstraintViolation<Patient> violation = violations.iterator().next();
            assertThat(violation.getPropertyPath().toString()).isEqualTo("id");
            assertThat(violation.getMessage()).contains("must be null");
        }
    }

    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("Should set and get id correctly")
        void shouldSetAndGetIdCorrectly() {
            // Act
            patient.setId(1L);

            // Assert
            assertThat(patient.getId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("Should set and get firstName correctly")
        void shouldSetAndGetFirstNameCorrectly() {
            // Act
            patient.setFirstName("Jane");

            // Assert
            assertThat(patient.getFirstName()).isEqualTo("Jane");
        }

        @Test
        @DisplayName("Should set and get lastName correctly")
        void shouldSetAndGetLastNameCorrectly() {
            // Act
            patient.setLastName("Smith");

            // Assert
            assertThat(patient.getLastName()).isEqualTo("Smith");
        }

        @Test
        @DisplayName("Should set and get dateOfBirth correctly")
        void shouldSetAndGetDateOfBirthCorrectly() {
            // Act
            patient.setDateOfBirth("1985-05-15");

            // Assert
            assertThat(patient.getDateOfBirth()).isEqualTo("1985-05-15");
        }

        @Test
        @DisplayName("Should set and get gender correctly")
        void shouldSetAndGetGenderCorrectly() {
            // Act
            patient.setGender('F');

            // Assert
            assertThat(patient.getGender()).isEqualTo('F');
        }

        @Test
        @DisplayName("Should set and get address correctly")
        void shouldSetAndGetAddressCorrectly() {
            // Act
            patient.setAddress("456 Oak Ave");

            // Assert
            assertThat(patient.getAddress()).isEqualTo("456 Oak Ave");
        }

        @Test
        @DisplayName("Should set and get phone correctly")
        void shouldSetAndGetPhoneCorrectly() {
            // Act
            patient.setPhone("987-654-3210");

            // Assert
            assertThat(patient.getPhone()).isEqualTo("987-654-3210");
        }
    }

    @Nested
    @DisplayName("Lombok Tests")
    class LombokTests {

        @Test
        @DisplayName("Should generate equals and hashCode correctly")
        void shouldGenerateEqualsAndHashCodeCorrectly() {
            // Arrange
            Patient patient1 = new Patient();
            patient1.setId(1L);
            patient1.setFirstName("John");
            patient1.setLastName("Doe");
            patient1.setDateOfBirth("1990-01-01");
            patient1.setGender('M');

            Patient patient2 = new Patient();
            patient2.setId(1L);
            patient2.setFirstName("John");
            patient2.setLastName("Doe");
            patient2.setDateOfBirth("1990-01-01");
            patient2.setGender('M');

            // Assert
            assertThat(patient1).isEqualTo(patient2);
            assertThat(patient1.hashCode()).isEqualTo(patient2.hashCode());
        }

        @Test
        @DisplayName("Should generate toString correctly")
        void shouldGenerateToStringCorrectly() {
            // Act
            String result = patient.toString();

            // Assert
            assertThat(result).contains("Patient");
            assertThat(result).contains("firstName=John");
            assertThat(result).contains("lastName=Doe");
            assertThat(result).contains("dateOfBirth=1990-01-01");
            assertThat(result).contains("gender=M");
        }
    }

    @Nested
    @DisplayName("Edge Case Tests")
    class EdgeCaseTests {

        @Test
        @DisplayName("Should handle empty strings for optional fields")
        void shouldHandleEmptyStringsForOptionalFields() {
            // Arrange
            patient.setAddress("");
            patient.setPhone("");

            // Act
            Set<ConstraintViolation<Patient>> violations = validator.validate(patient);

            // Assert
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should handle different gender characters")
        void shouldHandleDifferentGenderCharacters() {
            // Arrange & Act & Assert
            patient.setGender('F');
            assertThat(validator.validate(patient)).isEmpty();

            patient.setGender('M');
            assertThat(validator.validate(patient)).isEmpty();

            patient.setGender('O');
            assertThat(validator.validate(patient)).isEmpty();
        }

        @Test
        @DisplayName("Should handle long strings for name fields")
        void shouldHandleLongStringsForNameFields() {
            // Arrange
            String longName = "A".repeat(255);
            patient.setFirstName(longName);
            patient.setLastName(longName);

            // Act
            Set<ConstraintViolation<Patient>> violations = validator.validate(patient);

            // Assert - No max length constraint defined, so it should pass
            assertThat(violations).isEmpty();
        }
    }
}
