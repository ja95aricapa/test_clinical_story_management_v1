package com.example.clinic.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.clinic.model.ClinicalRecord;
import com.example.clinic.model.Patient;
import com.example.clinic.repository.ClinicalRecordRepository;
import com.example.clinic.repository.PatientRepository;

/**
 * Clinical record service.
 */
@Service
public class ClinicalRecordService {

    @Autowired
    private ClinicalRecordRepository clinicalRecordRepository;

    @Autowired
    private PatientRepository patientRepository;

    public Page<ClinicalRecord> listRecords(Long patientId, Pageable pageable) {
        if (patientId == null) {
            return clinicalRecordRepository.findAll(pageable);
        }
        return clinicalRecordRepository.findByPatientId(patientId, pageable);
    }

    public Optional<ClinicalRecord> getRecord(Long id) {
        return clinicalRecordRepository.findById(id);
    }

    public ClinicalRecord createRecord(Long patientId, String symptoms, String diagnosis, String treatment) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        ClinicalRecord record = new ClinicalRecord();
        record.setPatient(patient);
        record.setSymptoms(symptoms);
        record.setDiagnosis(diagnosis);
        record.setTreatment(treatment);

        // createdAt is set automatically in @PrePersist
        return clinicalRecordRepository.save(record);
    }

    public ClinicalRecord updateRecord(Long id, String symptoms, String diagnosis, String treatment) {
        return clinicalRecordRepository.findById(id).map(record -> {
            record.setSymptoms(symptoms);
            record.setDiagnosis(diagnosis);
            record.setTreatment(treatment);
            return clinicalRecordRepository.save(record);
        }).orElseThrow(() -> new RuntimeException("Clinical record not found"));
    }

    public void deleteRecord(Long id) {
        clinicalRecordRepository.deleteById(id);
    }
}
