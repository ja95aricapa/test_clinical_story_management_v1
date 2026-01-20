package com.example.clinic.controller;

import com.example.clinic.model.ClinicalRecord;
import com.example.clinic.service.ClinicalRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/records")
public class ClinicalRecordController {

    @Autowired
    private ClinicalRecordService clinicalRecordService;

    @GetMapping
    public String listRecords(@RequestParam(required = false) Long patientId, Pageable pageable, Model model) {
        Page<ClinicalRecord> records = clinicalRecordService.listRecords(patientId, pageable);
        model.addAttribute("records", records);
        model.addAttribute("patientId", patientId);
        return "records/list";
    }

    @GetMapping("/{id}")
    public String viewRecord(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        return clinicalRecordService.getRecord(id).map(record -> {
            model.addAttribute("record", record);
            return "records/view";
        }).orElseGet(() -> {
            redirectAttributes.addFlashAttribute("error", "Record not found");
            return "redirect:/records";
        });
    }

    @PostMapping
    public String createRecord(@ModelAttribute ClinicalRecord record, RedirectAttributes redirectAttributes) {
        clinicalRecordService.createRecord(record);
        redirectAttributes.addFlashAttribute("success", "Record created successfully");
        return "redirect:/records";
    }

    @PostMapping("/{id}")
    public String updateRecord(@PathVariable Long id, @ModelAttribute ClinicalRecord record, RedirectAttributes redirectAttributes) {
        clinicalRecordService.updateRecord(id, record);
        redirectAttributes.addFlashAttribute("success", "Record updated successfully");
        return "redirect:/records";
    }

    @DeleteMapping("/{id}")
    public String deleteRecord(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        clinicalRecordService.deleteRecord(id);
        redirectAttributes.addFlashAttribute("success", "Record deleted successfully");
        return "redirect:/records";
    }
}