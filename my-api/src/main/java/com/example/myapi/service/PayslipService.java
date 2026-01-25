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

            // Fonts
            PDType1Font fontBold = PDType1Font.HELVETICA_BOLD;
            PDType1Font fontRegular = PDType1Font.HELVETICA;
            PDType1Font fontOblique = PDType1Font.HELVETICA_OBLIQUE;

            // Coordinates
            float yPosition = 750;
            float margin = 50;

            // ===== HEADER COMPANY =====
            content.beginText();
            content.setFont(fontBold, 18);
            content.newLineAtOffset(margin, yPosition);
            content.showText("Pezas & Znedi");
            content.endText();

            yPosition -= 40;

            // ===== TITRE =====
            content.beginText();
            content.setFont(fontBold, 22);
            content.newLineAtOffset(margin, yPosition);
            content.showText("FICHE DE PAIE - " + payslip.getMonth());
            content.endText();

            // Line under title
            yPosition -= 10;
            content.setLineWidth(1f);
            content.moveTo(margin, yPosition);
            content.lineTo(550, yPosition);
            content.stroke();

            // ===== INFORMATIONS EMPLOYE =====
            yPosition -= 50;

            content.beginText();
            content.setFont(fontBold, 14);
            content.newLineAtOffset(margin, yPosition);
            content.showText("Informations employé :");
            content.endText();

            yPosition -= 25;
            content.beginText();
            content.setFont(fontRegular, 12);
            content.newLineAtOffset(margin, yPosition);
            content.setLeading(20f);
            content.showText("Nom : " + payslip.getEmployee().getLastName());
            content.newLine();
            content.showText("Prénom : " + payslip.getEmployee().getFirstName());
            content.newLine();
            content.showText("Email : " + payslip.getEmployee().getEmail());
            content.endText();

            // ===== SALAIRE =====
            yPosition -= 100;

            content.beginText();
            content.setFont(fontBold, 14);
            content.newLineAtOffset(margin, yPosition);
            content.showText("Détail de la rémunération :");
            content.endText();

            yPosition -= 30;

            // Salaire Brut
            content.beginText();
            content.setFont(fontRegular, 12);
            content.newLineAtOffset(margin, yPosition);
            content.showText("Salaire Brut :");
            content.endText();

            String gross = String.format("%.2f €", payslip.getGrossSalary());
            float grossWidth = fontRegular.getStringWidth(gross) / 1000 * 12;
            content.beginText();
            content.setFont(fontRegular, 12);
            content.newLineAtOffset(550 - grossWidth, yPosition);
            content.showText(gross);
            content.endText();

            yPosition -= 20;

            // Charges
            content.beginText();
            content.setFont(fontRegular, 12);
            content.newLineAtOffset(margin, yPosition);
            content.showText("Charges sociales (20%) :");
            content.endText();

            double charges = payslip.getGrossSalary() - payslip.getNetSalary();
            String chargesStr = String.format("- %.2f €", charges);
            float chargesWidth = fontRegular.getStringWidth(chargesStr) / 1000 * 12;

            content.beginText();
            content.setFont(fontRegular, 12);
            content.newLineAtOffset(550 - chargesWidth, yPosition);
            content.showText(chargesStr);
            content.endText();

            // Separator line
            yPosition -= 10;
            content.setLineWidth(0.5f);
            content.moveTo(margin, yPosition);
            content.lineTo(550, yPosition);
            content.stroke();

            yPosition -= 30;

            // NET A PAYER
            content.beginText();
            content.setFont(fontBold, 16);
            content.newLineAtOffset(margin, yPosition);
            content.showText("NET À PAYER");
            content.endText();

            String net = String.format("%.2f €", payslip.getNetSalary());
            float netWidth = fontBold.getStringWidth(net) / 1000 * 16;

            content.beginText();
            content.setFont(fontBold, 16);
            content.newLineAtOffset(550 - netWidth, yPosition);
            content.showText(net);
            content.endText();

            // ===== FOOTER =====
            content.beginText();
            content.setFont(fontOblique, 10);
            content.newLineAtOffset(margin, 50);
            content.showText("Document généré automatiquement par Pezas & Znedi RH System.");
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
