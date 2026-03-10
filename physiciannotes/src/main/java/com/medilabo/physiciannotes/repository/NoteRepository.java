package com.medilabo.physiciannotes.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.medilabo.physiciannotes.domain.Note;

public interface NoteRepository extends MongoRepository<Note, String> {
    List<Note> findAllByPatId(Long patId);
}
