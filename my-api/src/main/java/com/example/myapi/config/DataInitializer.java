package com.example.myapi.config;

import com.example.myapi.model.Employee;
import com.example.myapi.repository.EmployeeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(EmployeeRepository employeeRepo) {
        return args -> {

            // S’il n’y a aucun employé, on en crée un RH par défaut
            if (employeeRepo.count() == 0) {

                Employee rh = new Employee();
                rh.setFirstName("Admin");
                rh.setLastName("RH");
                rh.setEmail("rh@gmail.com");
                rh.setJob("Responsable RH");
                rh.setMdp("rhAdmin");
                rh.setHours(0);
                rh.setHourlyRate(0.0);
                rh.setAbilities("RH");

                employeeRepo.save(rh);

                System.out.println("=== RH par défaut créé ===");
                System.out.println("Email : rh@gmail.com");
                System.out.println("Mot de passe : rhAdmin");
                System.out.println("==========================");
            }
        };
    }
}
