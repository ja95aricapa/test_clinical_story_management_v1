package com.example.clinic.repository;

import com.example.clinic.model.ClinicalRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClinicalRecordRepository extends JpaRepository<ClinicalRecord, Long> {
    Page<ClinicalRecord> findByPatientId(Long patientId, Pageable pageable);
}