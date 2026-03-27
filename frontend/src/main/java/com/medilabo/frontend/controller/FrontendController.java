package com.medilabo.frontend.controller;

import java.util.Objects;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

import com.medilabo.frontend.domain.Note;
import com.medilabo.frontend.domain.Patient;
import com.medilabo.frontend.service.FrontendService;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class FrontendController {
    private final FrontendService frontendService;

    public FrontendController(FrontendService frontendService) {
        this.frontendService = frontendService;
    }

    @GetMapping("/login")
    public String loginPage(@RequestParam(required = false) String error,
                            @RequestParam(required = false) String logout,
                            Model model) {
        if (error != null) {
            model.addAttribute("error", true);
        }
        if (logout != null) {
            model.addAttribute("logout", true);
        }
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session) {
        try {
            String token = frontendService.authenticate(username, password);
            if (token != null) {
                session.setAttribute("jwt", token);
                return "redirect:/";
            }
        } catch (Exception e) {
            log.error("Authentication failed", e);
        }
        return "redirect:/login?error";
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login?logout";
    }

    @GetMapping("/")
    public String indexPage(Model model) {
        model.addAttribute("message", "Hello, World!");
        return "index";
    }

    @GetMapping("/patient")
    public String patientPage(Model model) {
        model.addAttribute("patientList", frontendService.getAllPatients());
        return "patient";
    }

    @GetMapping("/patientNotes/{patId}")
    public String getPatientNotesPage(@PathVariable Long patId, Model model) {
        model.addAttribute("notes", frontendService.getPatientNotes(patId));
        model.addAttribute("patId", patId);
        model.addAttribute("diabetesRisk", frontendService.getDiabetesRiskAssessment(patId));
        return "patientNotes";
    }

    @GetMapping("/createPatientForm")
    public String createPatientFormPage(Model model) {
        model.addAttribute("patient", new Patient(null, "", "", "", null, "", ""));
        return "createPatientForm";
    }

    @GetMapping("/createNoteForm")
    public String createNoteFormPage(@RequestParam Long patId, Model model) {
        Patient patient = frontendService.getPatientById(patId);

        if (patient == null) {
            return "redirect:/patient";
        }

        String patientFullName = patient.firstName() + " " + patient.lastName();
        model.addAttribute("note", new Note(null, patId, patientFullName, null));
        return "createNoteForm";
    }

    @GetMapping("/updateNoteForm")
    public String updateNoteFormPage(
            @RequestParam String noteId,
            @RequestParam Long patId,
            Model model) {
        Note note = frontendService.getPatientNotes(patId)
                .stream()
                .filter(existingNote -> Objects.equals(existingNote.noteId(), noteId))
                .findFirst()
                .orElse(null);

        if (note == null) {
            return "redirect:/patientNotes/" + patId;
        }

        Patient patient = frontendService.getPatientById(patId);
        if (patient != null) {
            String patientFullName = patient.firstName() + " " + patient.lastName();
            note = new Note(note.noteId(), note.patId(), patientFullName, note.note());
        }

        model.addAttribute("note", note);
        return "updateNoteForm";
    }

    @GetMapping("/updatePatientForm")
    public String updatePatientFormPage(@RequestParam Long id, Model model) {
        Patient patient = frontendService.getAllPatients()
                .stream()
                .filter(existingPatient -> Objects.equals(existingPatient.id(), id))
                .findFirst()
                .orElse(null);

        if (patient == null) {
            return "redirect:/patient";
        }

        model.addAttribute("patient", patient);
        return "updatePatientForm";
    }

    @PostMapping("/patients")
    public String createPatient(@ModelAttribute("patient") Patient patient) {
        try {
            frontendService.createPatient(patient);
        } catch (Exception e) {
            log.error("Error creating patient", e);
        }
        return "redirect:/patient";
    }

    @PostMapping("/notes")
    public String createNote(@ModelAttribute("note") Note note) {
        try {
            frontendService.createNote(note);
        } catch (Exception e) {
            log.error("Error creating note", e);
        }
        return "redirect:/patientNotes/" + note.patId();
    }

    @PostMapping("/notes/{noteId}")
    public String updateNote(@PathVariable String noteId, @ModelAttribute("note") Note note) {
        try {
            frontendService.updateNote(noteId, note);
        } catch (Exception e) {
            log.error("Error updating note", e);
        }
        return "redirect:/patientNotes/" + note.patId();
    }

    @PostMapping("/notes/{noteId}/delete")
    public String deleteNote(@PathVariable String noteId, @RequestParam Long patId) {
        try {
            frontendService.deleteNote(noteId);
        } catch (Exception e) {
            log.error("Error deleting note", e);
        }
        return "redirect:/patientNotes/" + patId;
    }

    @PutMapping("/patients/{id}")
    public String updatePatient(@PathVariable Long id, @ModelAttribute("patient") Patient patient) {
        Patient patientToUpdate = new Patient(
                null,
                patient.firstName(),
                patient.lastName(),
                patient.dateOfBirth(),
                patient.gender(),
                patient.address(),
                patient.phone());

        try {
            frontendService.updatePatient(id, patientToUpdate);
        } catch (Exception e) {
            log.error("Error updating patient", e);
        }
        return "redirect:/patient";
    }

    @PostMapping("/patients/{id}/delete")
    public String deletePatient(@PathVariable Long id) {
        try {
            frontendService.deletePatient(id);
        } catch (Exception e) {
            log.error("Error deleting patient", e);
        }
        return "redirect:/patient";
    }

}
