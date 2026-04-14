package com.medilabo.diabetesassessment.service;

import com.medilabo.diabetesassessment.domain.Note;
import com.medilabo.diabetesassessment.domain.Patient;
import com.medilabo.diabetesassessment.exception.PatientNotFoundException;
import com.medilabo.diabetesassessment.repository.PatientRepository;
import com.medilabo.diabetesassessment.repository.PhysicianNoteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DiabetesServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private PhysicianNoteRepository physicianNoteRepository;

    @InjectMocks
    private DiabetesService diabetesService;

    // DOB well over 30 years in the past
    private static final String DOB_OVER_30 = "01-01-1960";
    // DOB well under 30 years ago
    private static final String DOB_UNDER_30 = "01-01-2005";

    private Patient createPatient(Long id, char gender, String dob) {
        Patient patient = new Patient();
        patient.setId(id);
        patient.setFirstName("John");
        patient.setLastName("Doe");
        patient.setGender(gender);
        patient.setDateOfBirth(dob);
        return patient;
    }

    private Note createNote(Long patId, String... keywords) {
        Note note = new Note();
        note.setPatId(patId);
        note.setPatient("John Doe");
        note.setNote(String.join(" ", keywords));
        return note;
    }

    // ── Patient not found ─────────────────────────────────────────────────────

    @Test
    void calculateRisk_PatientNotFound_ThrowsPatientNotFoundException() {
        when(patientRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> diabetesService.calculateRisk(1L))
                .isInstanceOf(PatientNotFoundException.class)
                .hasMessage("Patient not found");
    }

    // ── No physician notes ────────────────────────────────────────────────────

    @Test
    void calculateRisk_NoNotes_ReturnsNoNotesMessage() {
        Patient patient = createPatient(1L, 'M', DOB_OVER_30);
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(physicianNoteRepository.findAllByPatId(1L)).thenReturn(Collections.emptyList());

        String result = diabetesService.calculateRisk(1L);

        assertThat(result).contains("No physician notes found");
        assertThat(result).contains("John").contains("Doe");
    }

    // ── Risk count 0 / 1 → None ───────────────────────────────────────────────

    @Test
    void calculateRisk_ZeroKeywords_ReturnsNone() {
        Patient patient = createPatient(1L, 'M', DOB_OVER_30);
        Note note = createNote(1L, "Patient appears healthy overall");
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(physicianNoteRepository.findAllByPatId(1L)).thenReturn(List.of(note));

        assertThat(diabetesService.calculateRisk(1L)).isEqualTo("None");
    }

    @Test
    void calculateRisk_OneKeyword_ReturnsNone() {
        Patient patient = createPatient(1L, 'M', DOB_OVER_30);
        Note note = createNote(1L, "Smoking noticed during visit");
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(physicianNoteRepository.findAllByPatId(1L)).thenReturn(List.of(note));

        assertThat(diabetesService.calculateRisk(1L)).isEqualTo("None");
    }

    // ── Age > 30, risk count 2–5 → Borderline ────────────────────────────────

    @Test
    void calculateRisk_OlderThan30_TwoKeywords_ReturnsBorderline() {
        Patient patient = createPatient(1L, 'M', DOB_OVER_30);
        Note note = createNote(1L, "Smoking Cholesterol");
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(physicianNoteRepository.findAllByPatId(1L)).thenReturn(List.of(note));

        assertThat(diabetesService.calculateRisk(1L)).isEqualTo("Borderline");
    }

    @Test
    void calculateRisk_OlderThan30_FiveKeywords_ReturnsBorderline() {
        Patient patient = createPatient(1L, 'F', DOB_OVER_30);
        Note note = createNote(1L, "Smoking Cholesterol Height Weight Abnormal");
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(physicianNoteRepository.findAllByPatId(1L)).thenReturn(List.of(note));

        assertThat(diabetesService.calculateRisk(1L)).isEqualTo("Borderline");
    }

    // ── Male, age < 30 ────────────────────────────────────────────────────────

    @Test
    void calculateRisk_MaleUnder30_TwoKeywords_ReturnsNone() {
        Patient patient = createPatient(1L, 'M', DOB_UNDER_30);
        Note note = createNote(1L, "Smoking Cholesterol");
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(physicianNoteRepository.findAllByPatId(1L)).thenReturn(List.of(note));

        assertThat(diabetesService.calculateRisk(1L)).isEqualTo("None");
    }

    @Test
    void calculateRisk_MaleUnder30_ThreeKeywords_ReturnsInDanger() {
        Patient patient = createPatient(1L, 'M', DOB_UNDER_30);
        Note note = createNote(1L, "Smoking Cholesterol Height");
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(physicianNoteRepository.findAllByPatId(1L)).thenReturn(List.of(note));

        assertThat(diabetesService.calculateRisk(1L)).isEqualTo("In Danger");
    }

    @Test
    void calculateRisk_MaleUnder30_FourKeywords_ReturnsInDanger() {
        Patient patient = createPatient(1L, 'M', DOB_UNDER_30);
        Note note = createNote(1L, "Smoking Cholesterol Height Weight");
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(physicianNoteRepository.findAllByPatId(1L)).thenReturn(List.of(note));

        assertThat(diabetesService.calculateRisk(1L)).isEqualTo("In Danger");
    }

    @Test
    void calculateRisk_MaleUnder30_FiveKeywords_ReturnsEarlyOnset() {
        Patient patient = createPatient(1L, 'M', DOB_UNDER_30);
        Note note = createNote(1L, "Smoking Cholesterol Height Weight Abnormal");
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(physicianNoteRepository.findAllByPatId(1L)).thenReturn(List.of(note));

        assertThat(diabetesService.calculateRisk(1L)).isEqualTo("Early Onset");
    }

    @Test
    void calculateRisk_MaleUnder30_SixKeywords_ReturnsEarlyOnset() {
        Patient patient = createPatient(1L, 'M', DOB_UNDER_30);
        Note note = createNote(1L, "Smoking Cholesterol Height Weight Abnormal Dizziness");
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(physicianNoteRepository.findAllByPatId(1L)).thenReturn(List.of(note));

        assertThat(diabetesService.calculateRisk(1L)).isEqualTo("Early Onset");
    }

    // ── Male, age > 30 ────────────────────────────────────────────────────────

    @Test
    void calculateRisk_MaleOver30_SixKeywords_ReturnsInDanger() {
        Patient patient = createPatient(1L, 'M', DOB_OVER_30);
        Note note = createNote(1L, "Smoking Cholesterol Height Weight Abnormal Dizziness");
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(physicianNoteRepository.findAllByPatId(1L)).thenReturn(List.of(note));

        assertThat(diabetesService.calculateRisk(1L)).isEqualTo("In Danger");
    }

    @Test
    void calculateRisk_MaleOver30_SevenKeywords_ReturnsInDanger() {
        Patient patient = createPatient(1L, 'M', DOB_OVER_30);
        Note note = createNote(1L, "Smoking Cholesterol Height Weight Abnormal Dizziness Relapse");
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(physicianNoteRepository.findAllByPatId(1L)).thenReturn(List.of(note));

        assertThat(diabetesService.calculateRisk(1L)).isEqualTo("In Danger");
    }

    @Test
    void calculateRisk_MaleOver30_EightKeywords_ReturnsEarlyOnset() {
        Patient patient = createPatient(1L, 'M', DOB_OVER_30);
        Note note = createNote(1L, "Smoking Cholesterol Height Weight Abnormal Dizziness Relapse Reaction");
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(physicianNoteRepository.findAllByPatId(1L)).thenReturn(List.of(note));

        assertThat(diabetesService.calculateRisk(1L)).isEqualTo("Early Onset");
    }

    // ── Female, age < 30 ──────────────────────────────────────────────────────

    @Test
    void calculateRisk_FemaleUnder30_TwoKeywords_ReturnsNone() {
        Patient patient = createPatient(1L, 'F', DOB_UNDER_30);
        Note note = createNote(1L, "Smoking Cholesterol");
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(physicianNoteRepository.findAllByPatId(1L)).thenReturn(List.of(note));

        assertThat(diabetesService.calculateRisk(1L)).isEqualTo("None");
    }

    @Test
    void calculateRisk_FemaleUnder30_ThreeKeywords_ReturnsNone() {
        Patient patient = createPatient(1L, 'F', DOB_UNDER_30);
        Note note = createNote(1L, "Smoking Cholesterol Height");
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(physicianNoteRepository.findAllByPatId(1L)).thenReturn(List.of(note));

        assertThat(diabetesService.calculateRisk(1L)).isEqualTo("None");
    }

    @Test
    void calculateRisk_FemaleUnder30_FourKeywords_ReturnsInDanger() {
        Patient patient = createPatient(1L, 'F', DOB_UNDER_30);
        Note note = createNote(1L, "Smoking Cholesterol Height Weight");
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(physicianNoteRepository.findAllByPatId(1L)).thenReturn(List.of(note));

        assertThat(diabetesService.calculateRisk(1L)).isEqualTo("In Danger");
    }

    @Test
    void calculateRisk_FemaleUnder30_FiveKeywords_ReturnsInDanger() {
        Patient patient = createPatient(1L, 'F', DOB_UNDER_30);
        Note note = createNote(1L, "Smoking Cholesterol Height Weight Abnormal");
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(physicianNoteRepository.findAllByPatId(1L)).thenReturn(List.of(note));

        assertThat(diabetesService.calculateRisk(1L)).isEqualTo("In Danger");
    }

    @Test
    void calculateRisk_FemaleUnder30_SixKeywords_ReturnsEarlyOnset() {
        Patient patient = createPatient(1L, 'F', DOB_UNDER_30);
        Note note = createNote(1L, "Smoking Cholesterol Height Weight Abnormal Dizziness");
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(physicianNoteRepository.findAllByPatId(1L)).thenReturn(List.of(note));

        assertThat(diabetesService.calculateRisk(1L)).isEqualTo("Early Onset");
    }

    // ── Female, age > 30 ──────────────────────────────────────────────────────

    @Test
    void calculateRisk_FemaleOver30_SixKeywords_ReturnsInDanger() {
        Patient patient = createPatient(1L, 'F', DOB_OVER_30);
        Note note = createNote(1L, "Smoking Cholesterol Height Weight Abnormal Dizziness");
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(physicianNoteRepository.findAllByPatId(1L)).thenReturn(List.of(note));

        assertThat(diabetesService.calculateRisk(1L)).isEqualTo("In Danger");
    }

    @Test
    void calculateRisk_FemaleOver30_EightKeywords_ReturnsEarlyOnset() {
        Patient patient = createPatient(1L, 'F', DOB_OVER_30);
        Note note = createNote(1L, "Smoking Cholesterol Height Weight Abnormal Dizziness Relapse Reaction");
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(physicianNoteRepository.findAllByPatId(1L)).thenReturn(List.of(note));

        assertThat(diabetesService.calculateRisk(1L)).isEqualTo("Early Onset");
    }

    // ── Additional edge cases ─────────────────────────────────────────────────

    @Test
    void calculateRisk_KeywordMatchingIsCaseInsensitive() {
        Patient patient = createPatient(1L, 'M', DOB_OVER_30);
        Note note = createNote(1L, "smoking CHOLESTEROL");
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(physicianNoteRepository.findAllByPatId(1L)).thenReturn(List.of(note));

        assertThat(diabetesService.calculateRisk(1L)).isEqualTo("Borderline");
    }

    @Test
    void calculateRisk_DateOfBirthWithSlashDelimiter_ParsedCorrectly() {
        Patient patient = createPatient(1L, 'M', "01/01/1960");
        Note note = createNote(1L, "Smoking Cholesterol");
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(physicianNoteRepository.findAllByPatId(1L)).thenReturn(List.of(note));

        assertThat(diabetesService.calculateRisk(1L)).isEqualTo("Borderline");
    }

    @Test
    void calculateRisk_MultipleNotes_KeywordsAggregatedAcrossNotes() {
        Patient patient = createPatient(1L, 'M', DOB_OVER_30);
        Note note1 = createNote(1L, "Smoking Cholesterol");
        Note note2 = createNote(1L, "Height Weight Abnormal Dizziness Relapse Reaction");
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(physicianNoteRepository.findAllByPatId(1L)).thenReturn(List.of(note1, note2));

        assertThat(diabetesService.calculateRisk(1L)).isEqualTo("Early Onset");
    }

    @Test
    void calculateRisk_AllElevenKeywordsPresent_ReturnsEarlyOnset() {
        Patient patient = createPatient(1L, 'M', DOB_OVER_30);
        Note note = createNote(1L,
                "Hemoglobin A1C", "Microalbumin", "Height", "Weight", "Smoking",
                "Abnormal", "Cholesterol", "Dizziness", "Relapse", "Reaction", "Antibody");
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(physicianNoteRepository.findAllByPatId(1L)).thenReturn(List.of(note));

        assertThat(diabetesService.calculateRisk(1L)).isEqualTo("Early Onset");
    }
}
