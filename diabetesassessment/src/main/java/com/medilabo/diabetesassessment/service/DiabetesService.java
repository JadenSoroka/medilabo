package com.medilabo.diabetesassessment.service;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Service;

import com.medilabo.diabetesassessment.Constants;
import com.medilabo.diabetesassessment.domain.Note;
import com.medilabo.diabetesassessment.domain.Patient;
import com.medilabo.diabetesassessment.exception.PatientNotFoundException;
import com.medilabo.diabetesassessment.repository.PatientRepository;
import com.medilabo.diabetesassessment.repository.PhysicianNoteRepository;

@Service
public class DiabetesService {

    private final PatientRepository patientRepository;
    private final PhysicianNoteRepository physicianNoteRepository;

    public DiabetesService(PatientRepository patientRepository, PhysicianNoteRepository physicianNoteRepository) {
        this.patientRepository = patientRepository;
        this.physicianNoteRepository = physicianNoteRepository;
    }

    public String calculateRisk(Long patId) {
        Patient patient = patientRepository.findById(patId).orElseThrow(() -> new PatientNotFoundException("Patient not found"));
        List<Note> physicianNoteEntries = physicianNoteRepository.findAllByPatId(patId);
        List<String> notesOnPatient = physicianNoteEntries.stream().map(Note::getNote).toList();
        
        if (notesOnPatient.isEmpty()) {
            return "No physician notes found for patient " + patient.getFirstName() + " " + patient.getLastName() + ". Unable to assess diabetes risk.";
        }

        int age = calculateAge(patient.getDateOfBirth());
        char gender = patient.getGender();
        
        int riskCount = 0;
        for (String note : notesOnPatient) {
            System.out.println(note);
            for (String keyword : Constants.DIABETES_RISK_KEYWORDS) {
                if (note.contains(keyword)) {
                    riskCount++;
                }
            }
        }
        
        String riskLevel = "None";
        if (riskCount < 2) {
            riskLevel = "None";
        } else if (riskCount <= 5) {
            riskLevel = "Borderline";
        } else if (gender == 'M') {
            if ((age < 30 && (riskCount == 3 || riskCount == 4)) || (age > 30 && riskCount >= 6)) {
                riskLevel = "In Danger";
            } else if ((age < 30 && riskCount >= 5) || (age > 30 && riskCount >= 8)) {
                riskLevel = "Early Onset";
            }
        } else if (gender == 'F') {
            if ((age < 30 && (riskCount == 4 || riskCount == 5)) || (age > 30 && riskCount >= 6)) {
                riskLevel = "In Danger";
            } else if ((age < 30 && riskCount >= 6) || (age > 30 && riskCount >= 8)) {
                riskLevel = "Early Onset";
            }
        }        
        return riskLevel;
    }

    private int calculateAge(String dateOfBirth) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
        LocalDate birthDate = LocalDate.parse(dateOfBirth.replace('/', '-'), formatter);
        LocalDate currentDate = LocalDate.now();
        int age = Period.between(birthDate, currentDate).getYears();
        return age;
    }

}
