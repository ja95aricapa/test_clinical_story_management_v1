package com.example.clinic.service;

import com.example.clinic.model.ClinicalRecord;
import com.example.clinic.repository.ClinicalRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class ClinicalRecordService {

    @Autowired
    private ClinicalRecordRepository clinicalRecordRepository;

    public Page<ClinicalRecord> listRecords(Long patientId, Pageable pageable) {
        if (patientId == null) {
            return clinicalRecordRepository.findAll(pageable);
        }
        return clinicalRecordRepository.findByPatientId(patientId, pageable);
    }

    public Optional<ClinicalRecord> getRecord(Long id) {
        return clinicalRecordRepository.findById(id);
    }

    public ClinicalRecord createRecord(ClinicalRecord record) {
        return clinicalRecordRepository.save(record);
    }

    public ClinicalRecord updateRecord(Long id, ClinicalRecord updatedRecord) {
        return clinicalRecordRepository.findById(id).map(record -> {
            record.setSymptoms(updatedRecord.getSymptoms());
            record.setDiagnosis(updatedRecord.getDiagnosis());
            record.setTreatment(updatedRecord.getTreatment());
            return clinicalRecordRepository.save(record);
        }).orElseThrow(() -> new RuntimeException("Clinical record not found"));
    }

    public void deleteRecord(Long id) {
        clinicalRecordRepository.deleteById(id);
    }
}