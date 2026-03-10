package com.medilabo.physiciannotes.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.medilabo.physiciannotes.domain.Note;
import com.medilabo.physiciannotes.exception.NoteNotFoundException;
import com.medilabo.physiciannotes.repository.NoteRepository;

@ExtendWith(MockitoExtension.class)
class NoteServiceTests {

    @Mock
    private NoteRepository noteRepository;

    @InjectMocks
    private NoteService noteService;

    @Test
    void getAllNotesReturnsRepositoryResults() {
        Note note = buildNote("n1", 1L, "John Doe", "sample note");
        when(noteRepository.findAll()).thenReturn(List.of(note));

        List<Note> result = noteService.getAllNotes();

        assertEquals(1, result.size());
        assertEquals("n1", result.get(0).getNoteId());
        verify(noteRepository).findAll();
    }

    @Test
    void getNoteByPatIdReturnsRepositoryResults() {
        Note note = buildNote("n1", 12L, "John Doe", "checkup completed");
        when(noteRepository.findAllByPatId(12L)).thenReturn(List.of(note));

        List<Note> result = noteService.getNoteByPatId(12L);

        assertEquals(1, result.size());
        assertEquals(12L, result.get(0).getPatId());
        verify(noteRepository).findAllByPatId(12L);
    }

    @Test
    void createNoteDelegatesToRepositorySave() {
        Note newNote = buildNote(null, 3L, "Jane Doe", "new symptoms");

        noteService.createNote(newNote);

        verify(noteRepository).save(newNote);
    }

    @Test
    void updateNoteByNoteIdUpdatesAndSavesExistingNote() {
        Note existing = buildNote("n1", 5L, "Old Name", "old note");
        Note updated = buildNote("n2", 8L, "New Name", "updated note");
        when(noteRepository.findById("n1")).thenReturn(Optional.of(existing));

        noteService.updateNoteByNoteId("n1", updated);

        assertEquals(8L, existing.getPatId());
        assertEquals("New Name", existing.getPatient());
        assertEquals("updated note", existing.getNote());
        verify(noteRepository).save(existing);
    }

    @Test
    void updateNoteByNoteIdThrowsWhenMissing() {
        when(noteRepository.findById("missing")).thenReturn(Optional.empty());

        NoteNotFoundException ex = assertThrows(
                NoteNotFoundException.class,
                () -> noteService.updateNoteByNoteId("missing", buildNote(null, 1L, "X", "Y")));

        assertEquals("Note not found for noteId: missing", ex.getMessage());
        verify(noteRepository, times(0)).save(org.mockito.ArgumentMatchers.any(Note.class));
    }

    @Test
    void deleteNoteByNoteIdDeletesExistingNote() {
        Note existing = buildNote("n3", 9L, "Patient", "to delete");
        when(noteRepository.findById("n3")).thenReturn(Optional.of(existing));

        noteService.deleteNoteByNoteId("n3");

        verify(noteRepository).delete(existing);
    }

    @Test
    void deleteNoteByNoteIdThrowsWhenMissing() {
        when(noteRepository.findById("missing")).thenReturn(Optional.empty());

        NoteNotFoundException ex = assertThrows(
                NoteNotFoundException.class,
                () -> noteService.deleteNoteByNoteId("missing"));

        assertEquals("Note not found for noteId: missing", ex.getMessage());
        verify(noteRepository, times(0)).delete(org.mockito.ArgumentMatchers.any(Note.class));
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