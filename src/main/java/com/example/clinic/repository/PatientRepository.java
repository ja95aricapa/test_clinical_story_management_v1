package com.example.clinic.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.clinic.model.Patient;

public interface PatientRepository extends JpaRepository<Patient, Long> {

    Page<Patient> findByFullNameContainingIgnoreCaseOrDocumentIdContainingIgnoreCase(
            String fullName,
            String documentId,
            Pageable pageable
    );

    boolean existsByDocumentId(String documentId);
}
