package com.medilabo.physiciannotes.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.medilabo.physiciannotes.domain.Note;
import com.medilabo.physiciannotes.service.NoteService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;


@RestController
@RequestMapping("/api/notes")
@Slf4j
@Validated
public class NoteController {

    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @GetMapping
    public ResponseEntity<List<Note>> getAllNotes() {
        log.info("/api/notes GET request received");

        List<Note> notes = noteService.getAllNotes();

        log.info("/api/notes GET request successful, returning {} notes", notes.size());
        return ResponseEntity.ok(notes);
    }
    

    @GetMapping("/{patId}")
    public ResponseEntity<List<Note>> readNoteById(@PathVariable Long patId) {
        log.info("/api/notes GET request for patId: {}", patId);

        List<Note> notes = noteService.getNoteByPatId(patId);

        log.info("/api/notes GET request for patId: {} successful", patId);
        return ResponseEntity.ok(notes);
    }

    @PostMapping
    public ResponseEntity<Void> createNote(@Valid @RequestBody Note newNote) {
        log.info("/api/notes POST request received for patId: {}", newNote.getPatId());

        noteService.createNote(newNote);

        log.info("/api/notes POST request for patId: {} successful", newNote.getPatId());
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{noteId}")
    public ResponseEntity<Void> updateNoteByNoteId(@PathVariable String noteId, @RequestBody Note updatedNote) {
        log.info("/api/notes PUT request for noteId: {}", noteId);

        noteService.updateNoteByNoteId(noteId, updatedNote);

        log.info("/api/notes PUT request for noteId: {} successful", noteId);
        return ResponseEntity.ok().build();
    }
    
    @DeleteMapping("/{noteId}")
    public ResponseEntity<Void> deleteNoteByNoteId(@PathVariable String noteId) {
        log.info("/api/notes DELETE request for noteId: {}", noteId);

        noteService.deleteNoteByNoteId(noteId);

        log.info("/api/notes DELETE request for noteId: {} successful", noteId);
        return ResponseEntity.ok().build();
    }
}
