package com.example.myapi.service;

import com.example.myapi.model.Employee;
import com.example.myapi.model.Payslip;
import com.example.myapi.repository.EmployeeRepository;
import com.example.myapi.repository.PayslipRepository;
import com.example.myapi.exeption.ResourceNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.File;
import java.util.List;

@Service
public class PayslipService {

    @Autowired
    private EmployeeRepository employeeRepo;

    @Autowired
    private PayslipRepository payslipRepo;

    // Toutes les fiches de paie (RH)
    public List<Payslip> getAllPayslips() {
        return payslipRepo.findAll();
    }

    // Fiche par ID
    public Payslip getPayslipById(long id) {
        return payslipRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payslip not found with id " + id));
    }

    // Fiches par employé
    public List<Payslip> getPayslipsByEmployee(Long employeeId) {
        Employee employee = employeeRepo.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id " + employeeId));
        return payslipRepo.findByEmployee(employee);
    }

    // Supprimer une fiche
    public void deletePayslip(long id) {
        Payslip payslip = payslipRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payslip not found with id " + id));
        payslipRepo.delete(payslip);
    }

    // Créer une fiche de paie pour un employé
    public Payslip createPayslip(Long employeeId, String month) {

        Employee employee = employeeRepo.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id " + employeeId));

        // Calcul salaire
        double rate = employee.getHourlyRate() != null ? employee.getHourlyRate() : 0.0;
        double grossSalary = employee.getHours() * rate;
        double netSalary = grossSalary * 0.8; // 20% charges

        // Création fiche
        Payslip payslip = new Payslip();
        payslip.setEmployee(employee);
        payslip.setMonth(month);
        payslip.setGrossSalary(grossSalary);
        payslip.setNetSalary(netSalary);

        // Sauvegarde pour obtenir l'ID
        Payslip saved = payslipRepo.save(payslip);

        // Génération PDF
        String pdfPath = generatePdf(saved);
        saved.setUrlPdf(pdfPath);

        return payslipRepo.save(saved);
    }

    // Génération PDF
    private String generatePdf(Payslip payslip) {
        try {
            String directory = "C:/payslips/";
            File dir = new File(directory);
            if (!dir.exists())
                dir.mkdirs();

            String filename = "payslip_" + payslip.getId() + ".pdf";
            String fullPath = directory + filename;

            PDDocument document = new PDDocument();
            PDPage page = new PDPage();
            document.addPage(page);

            PDPageContentStream content = new PDPageContentStream(document, page);

            // ===== TITRE =====
            content.beginText();
            content.setFont(PDType1Font.HELVETICA_BOLD, 26);
            content.newLineAtOffset(50, 750);
            content.showText("FICHE DE PAIE");
            content.endText();

            // Ligne sous le titre
            content.setLineWidth(1f);
            content.moveTo(50, 740);
            content.lineTo(550, 740);
            content.stroke();

            // ===== INFORMATIONS EMPLOYE =====
            float startY = 700;
            float lineHeight = 20f;
            float startX = 50;

            content.beginText();
            content.setFont(PDType1Font.HELVETICA_BOLD, 14);
            content.newLineAtOffset(startX, startY);
            content.showText("Informations employé :");
            content.endText();

            startY -= lineHeight;

            content.beginText();
            content.setFont(PDType1Font.HELVETICA, 12);
            content.newLineAtOffset(startX, startY);
            content.setLeading(lineHeight);
            content.showText("Nom : " + payslip.getEmployee().getLastName());
            content.newLine();
            content.showText("Prénom : " + payslip.getEmployee().getFirstName());
            content.newLine();
            content.showText("Mois : " + payslip.getMonth());
            content.endText();

            // ===== SALAIRE =====
            startY -= lineHeight * 4;
            content.beginText();
            content.setFont(PDType1Font.HELVETICA_BOLD, 14);
            content.newLineAtOffset(startX, startY);
            content.showText("Rémunération :");
            content.endText();

            startY -= lineHeight;

            content.beginText();
            content.setFont(PDType1Font.HELVETICA, 12);
            content.newLineAtOffset(startX, startY);
            content.setLeading(lineHeight);
            content.showText("Salaire brut : " + payslip.getGrossSalary() + " €");
            content.newLine();
            content.showText("Salaire net : " + payslip.getNetSalary() + " €");
            content.endText();

            // ===== FOOTER =====
            content.beginText();
            content.setFont(PDType1Font.HELVETICA_OBLIQUE, 10);
            content.newLineAtOffset(50, 50);
            content.showText("Document généré automatiquement par le système RH.");
            content.endText();

            content.close();
            document.save(fullPath);
            document.close();

            return fullPath;

        } catch (Exception e) {
            throw new RuntimeException("Erreur génération PDF : " + e.getMessage());
        }
    }
}
