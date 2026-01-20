package com.example.clinic.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.clinic.model.Patient;
import com.example.clinic.service.ClinicalRecordService;
import com.example.clinic.service.PatientService;

@Controller
@RequestMapping("/patients")
public class PatientController {

    @Autowired
    private PatientService patientService;

    @Autowired
    private ClinicalRecordService clinicalRecordService;

    @GetMapping
    public String listPatients(@RequestParam(required = false) String search,
                               Pageable pageable,
                               Model model) {
        Page<Patient> patients = patientService.listPatients(search, pageable);
        model.addAttribute("patients", patients);
        model.addAttribute("search", search);
        return "patients/list";
    }

    @GetMapping("/{id}")
    public String viewPatient(@PathVariable Long id,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        return patientService.getPatient(id).map(patient -> {
            model.addAttribute("patient", patient);

            Pageable recent = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
            model.addAttribute("recentRecords", clinicalRecordService.listRecords(id, recent));

            return "patients/view";
        }).orElseGet(() -> {
            redirectAttributes.addFlashAttribute("error", "Patient not found");
            return "redirect:/patients";
        });
    }

    @PostMapping
    public String createPatient(@ModelAttribute Patient patient,
                                RedirectAttributes redirectAttributes) {
        try {
            patientService.createPatient(patient);
            redirectAttributes.addFlashAttribute("success", "Patient created successfully");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", "A patient with that Document ID already exists.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to create patient.");
        }
        return "redirect:/patients";
    }

    @PutMapping("/{id}")
    public String updatePatient(@PathVariable Long id,
                                @ModelAttribute Patient patient,
                                RedirectAttributes redirectAttributes) {
        try {
            patientService.updatePatient(id, patient);
            redirectAttributes.addFlashAttribute("success", "Patient updated successfully");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", "A patient with that Document ID already exists.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update patient.");
        }
        return "redirect:/patients";
    }

    @DeleteMapping("/{id}")
    public String deletePatient(@PathVariable Long id,
                                RedirectAttributes redirectAttributes) {
        try {
            patientService.deletePatient(id);
            redirectAttributes.addFlashAttribute("success", "Patient deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to delete patient.");
        }
        return "redirect:/patients";
    }
}
