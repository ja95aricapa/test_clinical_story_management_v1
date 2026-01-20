package com.example.clinic.service;

import com.example.clinic.model.Patient;
import com.example.clinic.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.PageImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class PatientService {

    @Autowired
    private PatientRepository patientRepository;

    @Value("${use.database:true}")
    private boolean useDatabase;

    @Autowired
    private ResourceLoader resourceLoader;

    private static final Logger logger = LoggerFactory.getLogger(PatientService.class);

    private List<Patient> loadHardcodedPatients() {
        Resource resource = resourceLoader.getResource("classpath:data/hardcoded-data.json");
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(resource.getInputStream(), new TypeReference<List<Patient>>() {});
        } catch (IOException e) {
            logger.error("Failed to load hardcoded patients from JSON file", e);
            return Collections.emptyList();
        }
    }

    public Page<Patient> listPatients(String search, Pageable pageable) {
        if (search == null || search.isBlank()) {
            return patientRepository.findAll(pageable);
        }
        return patientRepository.findByFullNameContainingIgnoreCaseOrDocumentIdContainingIgnoreCase(search, search, pageable);
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