package com.example.myapi.service;

import com.example.myapi.model.Employee;
import com.example.myapi.model.Payslip;
import com.example.myapi.repository.EmployeeRepository;
import com.example.myapi.exeption.ResourceNotFoundException;
import com.example.myapi.repository.PayslipRepository;

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

    // Donne toutes les fiches de paies (pour RH)
    public List<Payslip> getAllPayslips(){ return payslipRepo.findAll();}

    // Donne une fiche de paie selon l'ID
    public Payslip getPayslipById(long id) {
        return payslipRepo.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Payslip not found with id " + id));
    }

    // Donne les fiches de paies d'un employé
    public List<Payslip> getPayslipsByEmployee(Long employeeId) {
        Employee employee = employeeRepo.findById(employeeId)
                .orElseThrow(()->new ResourceNotFoundException("Employee not found with id " + employeeId));
        return payslipRepo.findByEmployee(employee);
    }

    // Supprime une fiche de paie par l'ID
    public void deletePayslip(long id) {
        Payslip payslip = payslipRepo.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Payslip not found with id " + id));
        payslipRepo.delete(payslip);
    }

    //  Creer une fiche de paie
    public Payslip createPayslip(Long id, String month) {

        // Récupération de l'employé
        Employee employee = employeeRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Employé introuvable avec id : " + id));

        // CALCULS
        double grossSalary = employee.getSalary();
        double netSalary = grossSalary * 0.8; // 20% de charges

        // Création fiche
        Payslip p = new Payslip();
        p.setMonth(month);
        p.setGrossSalary(grossSalary);
        p.setNetSalary(netSalary);
        p.setEmployee(employee);

        // Enregistre dans la base (avant PDF pour avoir l'ID)
        Payslip saved = payslipRepo.save(p);

        // Génération PDF
        String pdfPath = generatePdf(saved);

        // Mise à jour avec l’URL du PDF
        saved.setUrlPdf(pdfPath);
        return payslipRepo.save(saved);
    }

    // Génère un PDF
    private String generatePdf(Payslip payslip) {
        try {
            String directory = "C:/payslips/";
            String filename = "payslip_" + payslip.getId() + ".pdf";
            String fullPath = directory + filename;

            // Crée le dossier si nécessaire
            File dir = new File(directory);
            if (!dir.exists()) {
                boolean created = dir.mkdirs();
                if (!created) {
                    throw new RuntimeException("Impossible de créer le dossier : " + directory);
                }
            }

            PDDocument document = new PDDocument();
            PDPage page = new PDPage();
            document.addPage(page);

            PDPageContentStream content = new PDPageContentStream(document, page);

            content.beginText();
            content.setFont(PDType1Font.HELVETICA_BOLD, 22);
            content.newLineAtOffset(50, 750);
            content.showText("FICHE DE PAIE");
            content.endText();

            content.beginText();
            content.setFont(PDType1Font.HELVETICA, 14);
            content.setLeading(20f);
            content.newLineAtOffset(50, 700);

            content.showText("Employé : " +
                    payslip.getEmployee().getFirstName() + " " +
                    payslip.getEmployee().getLastName());
            content.newLine();

            content.showText("Mois : " + payslip.getMonth());
            content.newLine();

            content.showText("Salaire brut : " + payslip.getGrossSalary() + " €");
            content.newLine();

            content.showText("Salaire net : " + payslip.getNetSalary() + " €");
            content.newLine();

            content.endText();
            content.close();

            document.save(fullPath);
            document.close();

            return fullPath;

        } catch (Exception e) {
            throw new RuntimeException("Erreur de génération du PDF : " + e.getMessage());
        }
    }
}

