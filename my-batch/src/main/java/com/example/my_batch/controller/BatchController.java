package com.example.my_batch.controller;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@RestController
@RequestMapping("/batch")
public class BatchController {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job payrollJob;

    // POST /batch/run?month=2025-12
    @PostMapping("/run")
    public ResponseEntity<String> runBatch(@RequestParam(required = false) String month) {
        try {
            JobParametersBuilder builder = new JobParametersBuilder();
            builder.addLong("time", System.currentTimeMillis());
            if (month != null) builder.addString("month", month);
            jobLauncher.run(payrollJob, builder.toJobParameters());
            return ResponseEntity.ok("Job lancé");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Erreur lancement job : " + e.getMessage());
        }
    }
    @Component
    public class BatchScheduler {

        @Autowired
        private JobLauncher jobLauncher;

        @Autowired
        private Job payrollJob;

        // 1er du mois à 02:00
        @Scheduled(cron = "0 0 2 1 * ?")
        public void runMonthly() {
            try {
                jobLauncher.run(payrollJob, new JobParametersBuilder().addLong("scheduledAt", System.currentTimeMillis()).toJobParameters());
                System.out.println("Batch mensuel lancé");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
