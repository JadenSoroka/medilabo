package com.medilabo.physiciannotes.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.medilabo.physiciannotes.config.SecurityConfig;
import com.medilabo.physiciannotes.domain.Note;
import com.medilabo.physiciannotes.exception.NoteNotFoundException;
import com.medilabo.physiciannotes.service.NoteService;
import tools.jackson.databind.ObjectMapper;

@WebMvcTest(NoteController.class)
@Import(SecurityConfig.class)
class NoteControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private NoteService noteService;

    @Test
    void getAllNotesReturnsOkAndBody() throws Exception {
        Note note = buildNote("n1", 4L, "John Doe", "regular follow-up");
        when(noteService.getAllNotes()).thenReturn(List.of(note));

        mockMvc.perform(get("/api/notes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].noteId").value("n1"))
                .andExpect(jsonPath("$[0].patId").value(4))
                .andExpect(jsonPath("$[0].patient").value("John Doe"));
    }

    @Test
    void getNoteByPatIdReturnsOkAndBody() throws Exception {
        Note note = buildNote("n2", 22L, "Jane Doe", "blood pressure elevated");
        when(noteService.getNoteByPatId(22L)).thenReturn(List.of(note));

        mockMvc.perform(get("/api/notes/22"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].patId").value(22))
                .andExpect(jsonPath("$[0].note").value("blood pressure elevated"));
    }

    @Test
    void createNoteWithValidPayloadReturnsOk() throws Exception {
        Note newNote = buildNote(null, 6L, "Mary Sue", "patient recovering");

        mockMvc.perform(post("/api/notes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newNote)))
                .andExpect(status().isOk());

        verify(noteService).createNote(any(Note.class));
    }

    @Test
    void createNoteWithInvalidPayloadReturnsValidationError() throws Exception {
        String invalidJson = "{\"patId\":null,\"patient\":\"\",\"note\":\"\"}";

        mockMvc.perform(post("/api/notes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Failed"))
                .andExpect(jsonPath("$.fieldErrors.patId").exists())
                .andExpect(jsonPath("$.fieldErrors.patient").exists())
                .andExpect(jsonPath("$.fieldErrors.note").exists());
    }

    @Test
    void updateNoteReturnsOk() throws Exception {
        Note updated = buildNote(null, 18L, "John Doe", "updated treatment plan");

        mockMvc.perform(put("/api/notes/n5")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk());

        verify(noteService).updateNoteByNoteId(eq("n5"), any(Note.class));
    }

    @Test
    void updateNoteReturnsNotFoundWhenMissing() throws Exception {
        Note updated = buildNote(null, 18L, "John Doe", "updated treatment plan");
        doThrow(new NoteNotFoundException("Note not found for noteId: n404"))
                .when(noteService)
                .updateNoteByNoteId(eq("n404"), any(Note.class));

        mockMvc.perform(put("/api/notes/n404")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Note not found for noteId: n404"));
    }

    @Test
    void deleteNoteReturnsOk() throws Exception {
        mockMvc.perform(delete("/api/notes/n3"))
                .andExpect(status().isOk());

        verify(noteService).deleteNoteByNoteId("n3");
    }

    @Test
    void deleteNoteReturnsNotFoundWhenMissing() throws Exception {
        doThrow(new NoteNotFoundException("Note not found for noteId: missing"))
                .when(noteService)
                .deleteNoteByNoteId("missing");

        mockMvc.perform(delete("/api/notes/missing"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Note not found for noteId: missing"));
    }

    @Test
    void nonApiPathRequiresAuthentication() throws Exception {
        mockMvc.perform(get("/internal-only"))
                .andExpect(status().isUnauthorized());
    }

    private Note buildNote(String noteId, Long patId, String patient, String noteValue) {
        Note note = new Note();
        note.setNoteId(noteId);
        note.setPatId(patId);
        note.setPatient(patient);
        note.setNote(noteValue);
        return note;
    }
}