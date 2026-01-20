package com.example.clinic.controller;

import com.example.clinic.model.Patient;
import com.example.clinic.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/patients")
public class PatientController {

    @Autowired
    private PatientService patientService;

    @GetMapping
    public String listPatients(@RequestParam(required = false) String search, Pageable pageable, Model model) {
        Page<Patient> patients = patientService.listPatients(search, pageable);
        model.addAttribute("patients", patients);
        model.addAttribute("search", search);
        return "patients/list";
    }

    @GetMapping("/{id}")
    public String viewPatient(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        return patientService.getPatient(id).map(patient -> {
            model.addAttribute("patient", patient);
            return "patients/view";
        }).orElseGet(() -> {
            redirectAttributes.addFlashAttribute("error", "Patient not found");
            return "redirect:/patients";
        });
    }

    @PostMapping
    public String createPatient(@ModelAttribute Patient patient, RedirectAttributes redirectAttributes) {
        patientService.createPatient(patient);
        redirectAttributes.addFlashAttribute("success", "Patient created successfully");
        return "redirect:/patients";
    }

    @PostMapping("/{id}")
    public String updatePatient(@PathVariable Long id, @ModelAttribute Patient patient, RedirectAttributes redirectAttributes) {
        patientService.updatePatient(id, patient);
        redirectAttributes.addFlashAttribute("success", "Patient updated successfully");
        return "redirect:/patients";
    }

    @PostMapping("/{id}/delete")
    public String deletePatient(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        patientService.deletePatient(id);
        redirectAttributes.addFlashAttribute("success", "Patient deleted successfully");
        return "redirect:/patients";
    }
}