package com.example.clinic.controller;

import com.example.clinic.model.ClinicalRecord;
import com.example.clinic.model.Patient;
import com.example.clinic.service.ClinicalRecordService;
import com.example.clinic.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/records")
public class ClinicalRecordController {

    @Autowired
    private ClinicalRecordService clinicalRecordService;

    @Autowired
    private PatientService patientService;

    @GetMapping
    public String listRecords(@RequestParam(required = false) Long patientId,
                              Pageable pageable,
                              Model model) {
        Page<ClinicalRecord> records = clinicalRecordService.listRecords(patientId, pageable);
        List<Patient> patientsList = patientService.listAllPatients();

        model.addAttribute("records", records);
        model.addAttribute("patientId", patientId);
        model.addAttribute("patientsList", patientsList);

        return "records/list";
    }

    @GetMapping("/{id}")
    public String viewRecord(@PathVariable Long id,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        return clinicalRecordService.getRecord(id).map(record -> {
            model.addAttribute("record", record);
            return "records/view";
        }).orElseGet(() -> {
            redirectAttributes.addFlashAttribute("error", "Record not found");
            return "redirect:/records";
        });
    }

    @PostMapping
    public String createRecord(@RequestParam Long patientId,
                               @RequestParam String symptoms,
                               @RequestParam String diagnosis,
                               @RequestParam String treatment,
                               RedirectAttributes redirectAttributes) {
        try {
            clinicalRecordService.createRecord(patientId, symptoms, diagnosis, treatment);
            redirectAttributes.addFlashAttribute("success", "Record created successfully");
            return "redirect:/records?patientId=" + patientId;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to create record: " + e.getMessage());
            return "redirect:/records";
        }
    }

    @PutMapping("/{id}")
    public String updateRecord(@PathVariable Long id,
                               @RequestParam String symptoms,
                               @RequestParam String diagnosis,
                               @RequestParam String treatment,
                               RedirectAttributes redirectAttributes) {
        try {
            Long patientId = clinicalRecordService.updateRecord(id, symptoms, diagnosis, treatment)
                    .getPatient()
                    .getId();

            redirectAttributes.addFlashAttribute("success", "Record updated successfully");
            return "redirect:/records?patientId=" + patientId;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update record: " + e.getMessage());
            return "redirect:/records";
        }
    }

    @DeleteMapping("/{id}")
    public String deleteRecord(@PathVariable Long id,
                               RedirectAttributes redirectAttributes) {
        try {
            Long patientId = clinicalRecordService.getRecord(id)
                    .map(r -> r.getPatient().getId())
                    .orElse(null);

            clinicalRecordService.deleteRecord(id);
            redirectAttributes.addFlashAttribute("success", "Record deleted successfully");

            if (patientId != null) {
                return "redirect:/records?patientId=" + patientId;
            }
            return "redirect:/records";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to delete record: " + e.getMessage());
            return "redirect:/records";
        }
    }
}
