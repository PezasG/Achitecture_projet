package com.example.myapi.controller;

import com.example.myapi.model.Payslip;
import com.example.myapi.service.PayslipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping("/api/payslips")
public class PayslipController {

    @Autowired
    private PayslipService payslipService;

    // Toutes les fiches (RH)
    @GetMapping("/all")
    public List<Payslip> getAllPayslips() {
        return payslipService.getAllPayslips();
    }

    // Fiche par ID
    @GetMapping("/{id}")
    public ResponseEntity<Payslip> getById(@PathVariable Long id) {
        return ResponseEntity.ok(payslipService.getPayslipById(id));
    }

    // Fiches par employé
    @GetMapping("/employee/{id}")
    public List<Payslip> getByEmployee(@PathVariable Long id) {
        return payslipService.getPayslipsByEmployee(id);
    }

    // Création d’une fiche pour un employé via POST (recommandé)
    @PostMapping("/create")
    public ResponseEntity<Payslip> createPayslip(@RequestBody Map<String, String> request) {
        try {
            Long employeeId = Long.parseLong(request.get("employeeId"));
            String month = request.get("month");
            Payslip created = payslipService.createPayslip(employeeId, month);
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // Supprimer par ID
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deletePayslip(@PathVariable Long id) {
        payslipService.deletePayslip(id);
        return ResponseEntity.noContent().build();
    }

    // Télécharger le PDF
    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> downloadPayslip(@PathVariable Long id) {
        Payslip p = payslipService.getPayslipById(id);

        if (p.getUrlPdf() == null || p.getUrlPdf().isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        try {
            Path file = Path.of(p.getUrlPdf());
            if (!Files.exists(file)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            Resource resource = new PathResource(file);
            String filename = file.getFileName().toString();
            String contentType = Files.probeContentType(file);
            if (contentType == null) contentType = "application/octet-stream";

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .body(resource);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
