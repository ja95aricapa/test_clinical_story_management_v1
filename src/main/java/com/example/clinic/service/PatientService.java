package com.example.clinic.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.clinic.model.Patient;
import com.example.clinic.repository.PatientRepository;

/**
 * Patient service (DB-only).
 * Contains basic CRUD operations for Patient.
 */
@Service
public class PatientService {

    @Autowired
    private PatientRepository patientRepository;

    public Page<Patient> listPatients(String search, Pageable pageable) {
        if (search == null || search.isBlank()) {
            return patientRepository.findAll(pageable);
        }
        return patientRepository.findByFullNameContainingIgnoreCaseOrDocumentIdContainingIgnoreCase(
                search, search, pageable
        );
    }

    public List<Patient> listAllPatients() {
        return patientRepository.findAll();
    }

    public Optional<Patient> getPatient(Long id) {
        return patientRepository.findById(id);
    }

    public Patient createPatient(Patient patient) {
        // Basic guard to avoid showing raw SQL constraint errors in the UI
        if (patient.getDocumentId() != null && patientRepository.existsByDocumentId(patient.getDocumentId())) {
            throw new IllegalArgumentException("Document ID already exists");
        }

        try {
            return patientRepository.save(patient);
        } catch (DataIntegrityViolationException e) {
            // Fallback in case of race conditions or unexpected unique constraint errors
            throw new IllegalArgumentException("Document ID already exists");
        }
    }

    public Patient updatePatient(Long id, Patient updatedPatient) {
        return patientRepository.findById(id).map(patient -> {

            // If user changed the documentId, validate uniqueness
            String newDoc = updatedPatient.getDocumentId();
            if (newDoc != null && !newDoc.equals(patient.getDocumentId())) {
                if (patientRepository.existsByDocumentId(newDoc)) {
                    throw new IllegalArgumentException("Document ID already exists");
                }
            }

            patient.setFullName(updatedPatient.getFullName());
            patient.setDocumentId(updatedPatient.getDocumentId());
            patient.setBirthDate(updatedPatient.getBirthDate());

            try {
                return patientRepository.save(patient);
            } catch (DataIntegrityViolationException e) {
                throw new IllegalArgumentException("Document ID already exists");
            }

        }).orElseThrow(() -> new RuntimeException("Patient not found"));
    }

    public void deletePatient(Long id) {
        patientRepository.deleteById(id);
    }
}
