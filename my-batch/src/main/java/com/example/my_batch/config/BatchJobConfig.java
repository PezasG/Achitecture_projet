package com.example.my_batch.config;

import com.example.my_batch.model.BatchResult;
import com.example.my_batch.model.Employee;
import com.example.my_batch.service.PayrollApiService;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDate;
import java.util.List;

@Configuration
public class BatchJobConfig {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private PayrollApiService payrollApiService;

    @Autowired
    private JavaMailSender javaMailSender;

    // Reader: on construit une ListItemReader depuis l'API (fresh at each job run
    // due to StepScope)
    @Bean
    @org.springframework.batch.core.configuration.annotation.StepScope
    public ListItemReader<Employee> employeeItemReader() {
        List<Employee> employees = payrollApiService.fetchAllEmployees();
        return new ListItemReader<>(employees);
    }

    // Processor: pour chaque employee, appelle l'API pour créer la fiche et
    // télécharger le PDF
    @Bean
    public ItemProcessor<Employee, BatchResult> payslipProcessor() {
        return employee -> {
            try {
                String month = LocalDate.now().getYear() + "-" + String.format("%02d", LocalDate.now().getMonthValue());

                // create payslip via API -> returns payslip DTO
                PayrollApiService.PayslipDto payslip = payrollApiService.createPayslipForEmployee(employee.getId(),
                        month);

                if (payslip == null || payslip.getId() == null) {
                    System.err.println("Échec création fiche pour employé ID: " + employee.getId());
                    return null;
                }

                // download PDF bytes
                byte[] pdfBytes = payrollApiService.downloadPayslipPdfBytes(payslip.getId());
                if (pdfBytes == null) {
                    System.err.println("Échec téléchargement PDF fiche ID: " + payslip.getId());
                    return null;
                }

                String filename = "payslip_" + payslip.getId() + ".pdf";

                return new BatchResult(employee, payslip.getId(), month, pdfBytes, filename);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        };
    }

    // Writer: envoi email avec l'attachement
    @Bean
    public ItemWriter<BatchResult> emailWriter() {
        return items -> {
            for (BatchResult r : items) {
                if (r == null)
                    continue;
                try {
                    payrollApiService.sendEmailWithAttachment(javaMailSender, r.getEmployee().getEmail(),
                            "Votre fiche de paie - " + r.getMonth(),
                            "Bonjour " + r.getEmployee().getFirstName() + " " + r.getEmployee().getLastName()
                                    + ",\n\nVeuillez trouver en pièce jointe votre fiche de paie du mois "
                                    + r.getMonth() + ".\n\nCordialement,\nLe service RH",
                            r.getFilename(), r.getPdfBytes());
                    System.out.println("Mail envoyé à " + r.getEmployee().getEmail());
                } catch (Exception ex) {
                    System.err.println("Erreur envoi mail à " + r.getEmployee().getEmail() + " : " + ex.getMessage());
                }
            }
        };
    }

    // Step
    @Bean
    public Step generateAndSendStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("generateAndSendStep", jobRepository)
                .<Employee, BatchResult>chunk(5, transactionManager)
                .reader(employeeItemReader())
                .processor(payslipProcessor())
                .writer(emailWriter())
                .build();
    }

    // Job
    @Bean
    public Job payrollJob(JobRepository jobRepository, Step generateAndSendStep) {
        return new JobBuilder("payrollJob", jobRepository)
                .start(generateAndSendStep)
                .build();
    }

    // helper to run from code
    public void launchJob(Job job) throws Exception {
        jobLauncher.run(job, new JobParametersBuilder().addLong("time", System.currentTimeMillis()).toJobParameters());
    }
}
