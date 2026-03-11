package com.medilabo.diabetesassessment.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.medilabo.diabetesassessment.domain.Note;

public interface PhysicianNoteRepository extends MongoRepository<Note, String> {
    List<Note> findAllByPatId(Long patId);
}
