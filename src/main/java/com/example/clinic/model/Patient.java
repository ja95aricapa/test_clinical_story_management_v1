package com.example.clinic.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Patient entity.
 * Stores basic patient identity data.
 */
@Entity
@Table(name = "patients")
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(unique = true, nullable = false)
    private String documentId;

    @NotBlank
    @Column(nullable = false)
    private String fullName;

    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(nullable = false)
    private LocalDate birthDate;

    /**
     * Convenience mapping:
     * deleting a patient will delete all related clinical records (simple CRUD behavior).
     */
    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ClinicalRecord> clinicalRecords = new ArrayList<>();

    public Patient() {}

    public Long getId() {
        return id;
    }

    public String getDocumentId() {
        return documentId;
    }

    public String getFullName() {
        return fullName;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public List<ClinicalRecord> getClinicalRecords() {
        return clinicalRecords;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public void setClinicalRecords(List<ClinicalRecord> clinicalRecords) {
        this.clinicalRecords = clinicalRecords;
    }
}
