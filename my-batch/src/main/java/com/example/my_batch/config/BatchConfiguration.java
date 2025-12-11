package com.example.my_batch.config;

import com.example.my_batch.model.Employee;
import com.example.my_batch.model.Payslip;
import com.example.my_batch.repository.EmployeeRepository;
import com.example.my_batch.repository.PayslipRepository;
import com.example.my_batch.util.EmailUtil;
import jakarta.persistence.EntityManagerFactory;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.SimpleJob;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.boot.CommandLineRunner;

import java.io.File;
import java.time.LocalDate;
import java.util.stream.StreamSupport;

@Configuration
@EnableBatchProcessing
@EnableScheduling
public class BatchConfiguration {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private PayslipRepository payslipRepository;

    // ------------------ READER ------------------
    @Bean
    public JpaPagingItemReader<Employee> employeeReader() {
        return new JpaPagingItemReaderBuilder<Employee>()
                .name("employeeReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("SELECT e FROM Employee e")
                .pageSize(20)
                .build();
    }

    // ------------------ PROCESSOR ------------------
    @Bean
    public ItemProcessor<Employee, Payslip> payslipProcessor() {
        return employee -> {
            try {
                String month = LocalDate.now().getYear() + "-" + String.format("%02d", LocalDate.now().getMonthValue());

                double gross = employee.getSalary();
                double net = gross * 0.8;

                Payslip p = new Payslip();
                p.setMonth(month);
                p.setGrossSalary(gross);
                p.setNetSalary(net);
                p.setEmployee(employee);

                Payslip saved = payslipRepository.save(p);

                String pdfPath = generatePdf(saved);

                saved.setUrlPdf(pdfPath);
                Payslip finalSaved = payslipRepository.save(saved);

                // Envoi mail
                try {
                    EmailUtil.sendPayslipEmailWithAttachment(mailSender, employee.getEmail(),
                            "Votre fiche de paie — " + month,
                            "Bonjour " + employee.getFirstName() + ",\n\nVeuillez trouver ci-joint votre fiche de paie du mois " + month + ".\n\nCordialement,\nRH",
                            pdfPath);
                } catch (Exception ex) {
                    System.err.println("Erreur envoi mail à " + employee.getEmail() + " : " + ex.getMessage());
                }

                return finalSaved;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        };
    }

    // ------------------ WRITER ------------------
    @Bean
    public ItemWriter<Payslip> payslipWriter() {
        return items -> {
            long count = StreamSupport.stream(items.spliterator(), false).filter(x -> x != null).count();
            System.out.println("Batch : " + count + " fiches traitées.");
        };
    }

    // ------------------ STEP ------------------
    @Bean
    public Step generatePayslipsStep() {
        return new StepBuilder("generatePayslipsStep", jobRepository)
                .<Employee, Payslip>chunk(20, transactionManager)
                .reader(employeeReader())
                .processor(payslipProcessor())
                .writer(payslipWriter())
                .transactionManager(transactionManager)
                .build();
    }

    // ------------------ JOB ------------------
    @Bean
    public Job monthlyPayslipJob() {
        SimpleJob job = new SimpleJob("monthlyPayslipJob");
        job.setJobRepository(jobRepository);
        job.addStep(generatePayslipsStep());
        return job;
    }

    // ------------------ SCHEDULER : 1er du mois à minuit ------------------
    @Scheduled(cron = "0 0 0 1 * ?")
    public void runMonthlyBatch() {
        try {
            System.out.println("▶ Lancement batch de génération des fiches de paie...");
            jobLauncher.run(monthlyPayslipJob(),
                    new JobParametersBuilder()
                            .addLong("startAt", System.currentTimeMillis())
                            .toJobParameters());
        } catch (Exception e) {
            System.err.println("Erreur batch : " + e.getMessage());
        }
    }

    @Bean
    public CommandLineRunner testBatchAtStartup() {
        return args -> {
            System.out.println("▶ Test batch démarré...");
            try {
                jobLauncher.run(monthlyPayslipJob(),
                        new JobParametersBuilder()
                                .addLong("testRunAt", System.currentTimeMillis())
                                .toJobParameters());
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Erreur lors du test batch : " + e.getMessage());
            }
        };
    }

    // ------------------ PDF GENERATION ------------------
    private String generatePdf(Payslip payslip) throws Exception {
        String directory = "C:/payslips/";
        File dir = new File(directory);
        if (!dir.exists()) dir.mkdirs();

        String filename = "payslip_" + payslip.getId() + ".pdf";
        String fullPath = directory + filename;

        PDDocument document = new PDDocument();
        PDPage page = new PDPage();
        document.addPage(page);

        PDPageContentStream content = new PDPageContentStream(document, page);

        float margin = 50;
        float yStart = 750;
        float lineHeight = 18f;

        content.beginText();
        content.setFont(PDType1Font.HELVETICA_BOLD, 22);
        content.newLineAtOffset(margin, yStart);
        content.showText("FICHE DE PAIE");
        content.endText();

        content.setLineWidth(1f);
        content.moveTo(margin, yStart - 8);
        content.lineTo(550, yStart - 8);
        content.stroke();

        float y = yStart - 40;
        content.beginText();
        content.setFont(PDType1Font.HELVETICA_BOLD, 12);
        content.newLineAtOffset(margin, y);
        content.showText("Employé : ");
        content.setFont(PDType1Font.HELVETICA, 12);
        content.newLineAtOffset(70, 0);
        content.showText(payslip.getEmployee().getFirstName() + " " + payslip.getEmployee().getLastName());
        content.endText();

        y -= 30;
        content.beginText();
        content.setFont(PDType1Font.HELVETICA_BOLD, 12);
        content.newLineAtOffset(margin, y);
        content.showText("Mois : ");
        content.setFont(PDType1Font.HELVETICA, 12);
        content.newLineAtOffset(50, 0);
        content.showText(payslip.getMonth());
        content.endText();

        y -= 20;
        content.beginText();
        content.setFont(PDType1Font.HELVETICA, 12);
        content.newLineAtOffset(margin, y);
        content.showText(String.format("Salaire brut : %.2f €", payslip.getGrossSalary()));
        content.newLineAtOffset(0, -lineHeight);
        content.showText(String.format("Salaire net  : %.2f €", payslip.getNetSalary()));
        content.endText();

        content.beginText();
        content.setFont(PDType1Font.HELVETICA_OBLIQUE, 9);
        content.newLineAtOffset(margin, 40);
        content.showText("Document généré automatiquement.");
        content.endText();

        content.close();
        document.save(fullPath);
        document.close();

        return fullPath;
    }
}
