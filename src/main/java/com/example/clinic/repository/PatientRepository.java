package com.example.clinic.repository;

import com.example.clinic.model.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    Page<Patient> findByFullNameContainingIgnoreCaseOrDocumentIdContainingIgnoreCase(String fullName, String documentId, Pageable pageable);
}