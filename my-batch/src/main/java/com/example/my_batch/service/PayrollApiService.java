package com.example.my_batch.service;

import com.example.my_batch.model.Employee;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import jakarta.mail.internet.MimeMessage;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PayrollApiService {

    private final RestTemplate restTemplate;
    private final String apiBase = "http://localhost:8080/api";

    public PayrollApiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // Récupère tous les employés via l'API
    public List<Employee> fetchAllEmployees() {
        try {
            ResponseEntity<Employee[]> resp = restTemplate.getForEntity(apiBase + "/employees/all", Employee[].class);
            return resp.getBody() == null ? List.of() : Arrays.asList(resp.getBody());
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération des employés: " + e.getMessage());
            return List.of();
        }
    }

    // Crée la fiche via l'API -> retourne l'objet JSON (map -> on mappe
    // manuellement)
    public PayslipDto createPayslipForEmployee(Long employeeId, String month) {
        // Envoi en POST avec JSON body: {"employeeId": "123", "month": "2025-01"}
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("employeeId", String.valueOf(employeeId));
        requestBody.put("month", month);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> resp = restTemplate.postForEntity(apiBase + "/payslips/create", requestEntity,
                    Map.class);
            Map<String, Object> m = resp.getBody();

            if (m == null)
                return null;
            Long id = m.get("id") instanceof Number ? ((Number) m.get("id")).longValue() : null;
            String urlPdf = m.get("urlPdf") != null ? m.get("urlPdf").toString() : null;
            return new PayslipDto(id, urlPdf, month);
        } catch (Exception e) {
            System.err.println("Erreur creation payslip pour emp=" + employeeId + " : " + e.getMessage());
            return null;
        }
    }

    // Télécharger les bytes via endpoint download (stream)
    public byte[] downloadPayslipPdfBytes(Long payslipId) {
        try {
            ResponseEntity<byte[]> resp = restTemplate.exchange(apiBase + "/payslips/download/" + payslipId,
                    HttpMethod.GET, null, byte[].class);
            return resp.getBody();
        } catch (Exception e) {
            System.err.println("Erreur téléchargement PDF id=" + payslipId + " : " + e.getMessage());
            return null;
        }
    }

    // Envoi d'email avec byte[] en pièce jointe
    public void sendEmailWithAttachment(JavaMailSender mailSender, String to, String subject, String text,
            String filename, byte[] bytes) throws Exception {

        MimeMessage msg = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(msg, true, "UTF-8");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text);

        if (bytes != null && bytes.length > 0) {
            ByteArrayResource bar = new ByteArrayResource(bytes);
            helper.addAttachment(filename, bar);
        }

        mailSender.send(msg);
    }

    // Simple DTO for payslip minimal
    public static class PayslipDto {
        private final Long id;
        private final String urlPdf;
        private final String month;

        public PayslipDto(Long id, String urlPdf, String month) {
            this.id = id;
            this.urlPdf = urlPdf;
            this.month = month;
        }

        public Long getId() {
            return id;
        }

        public String getUrlPdf() {
            return urlPdf;
        }

        public String getMonth() {
            return month;
        }
    }
}
