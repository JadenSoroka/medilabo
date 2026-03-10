package com.medilabo.physiciannotes.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.medilabo.physiciannotes.domain.Note;
import com.medilabo.physiciannotes.exception.NoteNotFoundException;
import com.medilabo.physiciannotes.repository.NoteRepository;

import jakarta.validation.Valid;

@Service
public class NoteService {

    private final NoteRepository noteRepository;

    public NoteService(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    public List<Note> getAllNotes() {
        return noteRepository.findAll();
    }

    public List<Note> getNoteByPatId(Long patId) {
        List<Note> notes = noteRepository.findAllByPatId(patId);
        if (notes == null || notes.isEmpty()) {
            throw new NoteNotFoundException("Note not found for patId: " + patId);
        }
        return notes;
    }

    public void createNote(@Valid Note newNote) {
        noteRepository.save(newNote);
    }
    
    public void updateNoteByNoteId(String noteId, Note updatedNote) {
        Note existingNote = noteRepository.findById(noteId)
                .orElseThrow(() -> new NoteNotFoundException("Note not found for noteId: " + noteId));

        existingNote.setPatId(updatedNote.getPatId());
        existingNote.setPatient(updatedNote.getPatient());
        existingNote.setNote(updatedNote.getNote());

        noteRepository.save(existingNote);
    }

    public void deleteNoteByNoteId(String noteId) {
        Note existingNote = noteRepository.findById(noteId)
                .orElseThrow(() -> new NoteNotFoundException("Note not found for noteId: " + noteId));
        noteRepository.delete(existingNote);
    }

}
