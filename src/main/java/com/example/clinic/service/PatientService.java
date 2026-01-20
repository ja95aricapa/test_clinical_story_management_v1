package com.example.clinic.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.clinic.model.Patient;
import com.example.clinic.repository.PatientRepository;

/**
 * Patient service (DB-only).
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
        return patientRepository.save(patient);
    }

    public Patient updatePatient(Long id, Patient updatedPatient) {
        return patientRepository.findById(id).map(patient -> {
            patient.setFullName(updatedPatient.getFullName());
            patient.setDocumentId(updatedPatient.getDocumentId());
            patient.setBirthDate(updatedPatient.getBirthDate());
            return patientRepository.save(patient);
        }).orElseThrow(() -> new RuntimeException("Patient not found"));
    }

    public void deletePatient(Long id) {
        patientRepository.deleteById(id);
    }
}
