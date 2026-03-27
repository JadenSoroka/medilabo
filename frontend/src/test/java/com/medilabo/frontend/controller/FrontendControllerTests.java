package com.medilabo.frontend.controller;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.List;

import com.medilabo.frontend.domain.DiabetesRiskResponseDTO;
import com.medilabo.frontend.domain.Note;
import com.medilabo.frontend.domain.Patient;
import com.medilabo.frontend.service.FrontendService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(FrontendController.class)
class FrontendControllerTests {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private FrontendService frontendService;

        private MockHttpSession authenticatedSession;

        @BeforeEach
        void setUp() {
                authenticatedSession = new MockHttpSession();
                authenticatedSession.setAttribute("jwt", "test-token");
        }

        // --- Index ---

        @Test
        void indexPageReturnsIndexViewWithMessage() throws Exception {
                mockMvc.perform(get("/").session(authenticatedSession))
                                .andExpect(status().isOk())
                                .andExpect(view().name("index"))
                                .andExpect(model().attribute("message", equalTo("Hello, World!")));
        }

        // --- Patients ---

        @Test
        void patientPageAddsPatientList() throws Exception {
                List<Patient> patients = List.of(
                                new Patient(1L, "Ana", "Trujillo", "12/12/1980", 'F', "London", "111-222"));

                when(frontendService.getAllPatients()).thenReturn(patients);

                mockMvc.perform(get("/patient").session(authenticatedSession))
                                .andExpect(status().isOk())
                                .andExpect(view().name("patient"))
                                .andExpect(model().attribute("patientList", patients));
        }

        @Test
        void createPatientFormPageAddsEmptyPatient() throws Exception {
                mockMvc.perform(get("/createPatientForm").session(authenticatedSession))
                                .andExpect(status().isOk())
                                .andExpect(view().name("createPatientForm"))
                                .andExpect(model().attributeExists("patient"));
        }

        @Test
        void updatePatientFormPageRedirectsWhenPatientMissing() throws Exception {
                when(frontendService.getAllPatients()).thenReturn(List.of());

                mockMvc.perform(get("/updatePatientForm").param("id", "99").session(authenticatedSession))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/patient"));
        }

        @Test
        void updatePatientFormPageAddsPatientWhenFound() throws Exception {
                Patient patient = new Patient(5L, "John", "Smith", "01/01/1970", 'M', "Paris", "333-444");
                when(frontendService.getAllPatients()).thenReturn(List.of(patient));

                mockMvc.perform(get("/updatePatientForm").param("id", "5").session(authenticatedSession))
                                .andExpect(status().isOk())
                                .andExpect(view().name("updatePatientForm"))
                                .andExpect(model().attribute("patient", patient));
        }

        @Test
        void createPatientCallsServiceAndRedirects() throws Exception {
                mockMvc.perform(post("/patients")
                                .param("firstName", "Jane")
                                .param("lastName", "Doe")
                                .param("dateOfBirth", "02/02/1990")
                                .param("gender", "F")
                                .param("address", "Boston")
                                .param("phone", "555-666").session(authenticatedSession))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/patient"));

                verify(frontendService).createPatient(any(Patient.class));
        }

        @Test
        void updatePatientCallsServiceWithNormalizedPatient() throws Exception {
                ArgumentCaptor<Patient> patientCaptor = ArgumentCaptor.forClass(Patient.class);

                mockMvc.perform(put("/patients/7")
                                .param("id", "7")
                                .param("firstName", "Sam")
                                .param("lastName", "Lee")
                                .param("dateOfBirth", "03/03/2000")
                                .param("gender", "O")
                                .param("address", "Berlin")
                                .param("phone", "777-888").session(authenticatedSession))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/patient"));

                verify(frontendService).updatePatient(eq(7L), patientCaptor.capture());
                Patient captured = patientCaptor.getValue();
                assertNull(captured.id());
                assertEquals("Sam", captured.firstName());
                assertEquals("Lee", captured.lastName());
                assertEquals("03/03/2000", captured.dateOfBirth());
                assertEquals(Character.valueOf('O'), captured.gender());
                assertEquals("Berlin", captured.address());
                assertEquals("777-888", captured.phone());
        }

        @Test
        void deletePatientCallsServiceAndRedirects() throws Exception {
                mockMvc.perform(post("/patients/12/delete").session(authenticatedSession))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/patient"));

                verify(frontendService).deletePatient(12L);
        }

        @Test
        void updatePatientDoesNotCallServiceWhenException() throws Exception {
                org.mockito.Mockito.doThrow(new RuntimeException("boom"))
                                .when(frontendService)
                                .updatePatient(eq(3L), any(Patient.class));

                mockMvc.perform(put("/patients/3")
                                .param("firstName", "Alex")
                                .param("lastName", "Kim")
                                .param("dateOfBirth", "04/04/1994")
                                .param("gender", "M")
                                .param("address", "Rome")
                                .param("phone", "000-111").session(authenticatedSession))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/patient"));

                verify(frontendService).updatePatient(eq(3L), any(Patient.class));
                verify(frontendService, never()).createPatient(any(Patient.class));
        }

        // --- Notes ---

        @Test
        void patientNotesPageAddsNotesAndPatId() throws Exception {
                List<Note> notes = List.of(new Note("n1", 3L, "John Doe", "Feeling well"));
                when(frontendService.getPatientNotes(3L)).thenReturn(notes);
                when(frontendService.getDiabetesRiskAssessment(3L))
                                .thenReturn(new DiabetesRiskResponseDTO(3L, "None"));

                mockMvc.perform(get("/patientNotes/3").session(authenticatedSession))
                                .andExpect(status().isOk())
                                .andExpect(view().name("patientNotes"))
                                .andExpect(model().attribute("notes", notes))
                                .andExpect(model().attribute("patId", 3L));
        }

        @Test
        void createNoteFormPageAddsEmptyNote() throws Exception {
                Patient patient = new Patient(5L, "John", "Doe", "01/01/1970", 'M', "Paris", "333-444");
                when(frontendService.getPatientById(5L)).thenReturn(patient);

                mockMvc.perform(get("/createNoteForm").param("patId", "5").session(authenticatedSession))
                                .andExpect(status().isOk())
                                .andExpect(view().name("createNoteForm"))
                                .andExpect(model().attribute("note", new Note(null, 5L, "John Doe", null)));
        }

        @Test
        void updateNoteFormPageAddsNoteWhenFound() throws Exception {
                Note note = new Note("n1", 5L, "John Doe", "Original note");
                Patient patient = new Patient(5L, "John", "Doe", "01/01/1970", 'M', "Paris", "333-444");
                when(frontendService.getPatientNotes(5L)).thenReturn(List.of(note));
                when(frontendService.getPatientById(5L)).thenReturn(patient);

                mockMvc.perform(get("/updateNoteForm").param("noteId", "n1").param("patId", "5").session(authenticatedSession))
                                .andExpect(status().isOk())
                                .andExpect(view().name("updateNoteForm"))
                                .andExpect(model().attribute("note", note));
        }

        @Test
        void updateNoteFormPageRedirectsWhenNoteNotFound() throws Exception {
                when(frontendService.getPatientNotes(5L)).thenReturn(List.of());

                mockMvc.perform(get("/updateNoteForm").param("noteId", "missing").param("patId", "5").session(authenticatedSession))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/patientNotes/5"));
        }

        @Test
        void createNoteCallsServiceAndRedirects() throws Exception {
                mockMvc.perform(post("/notes")
                                .param("patId", "5")
                                .param("patient", "John Doe")
                                .param("note", "Patient feeling better").session(authenticatedSession))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/patientNotes/5"));

                verify(frontendService).createNote(any(Note.class));
        }

        @Test
        void updateNoteCallsServiceAndRedirects() throws Exception {
                mockMvc.perform(post("/notes/n1")
                                .param("patId", "5")
                                .param("patient", "John Doe")
                                .param("note", "Updated note content").session(authenticatedSession))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/patientNotes/5"));

                verify(frontendService).updateNote(eq("n1"), any(Note.class));
        }

        @Test
        void deleteNoteCallsServiceAndRedirects() throws Exception {
                mockMvc.perform(post("/notes/n1/delete").param("patId", "5").session(authenticatedSession))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/patientNotes/5"));

                verify(frontendService).deleteNote("n1");
        }

        // --- Auth ---

        @Test
        void unauthenticatedRequestRedirectsToLogin() throws Exception {
                mockMvc.perform(get("/"))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/login"));
        }

        @Test
        void loginPageIsAccessibleWithoutSession() throws Exception {
                mockMvc.perform(get("/login"))
                                .andExpect(status().isOk())
                                .andExpect(view().name("login"));
        }

        @Test
        void loginPageShowsErrorParam() throws Exception {
                mockMvc.perform(get("/login").param("error", ""))
                                .andExpect(status().isOk())
                                .andExpect(view().name("login"))
                                .andExpect(model().attribute("error", true));
        }
}
