package com.example.myapi.config;

import com.example.myapi.model.Employee;
import com.example.myapi.model.User;
import com.example.myapi.repository.EmployeeRepository;
import com.example.myapi.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(UserRepository userRepo,
                                      EmployeeRepository employeeRepo) {
        return args -> {
            if (userRepo.count() == 0) {

                Employee e = new Employee();
                e.setFirstName("Gaetan");
                e.setLastName("Pezas");
                e.setJob("PDG");
                e.setHours(140);
                e.setSalary(40);
                e.setEmail("employee@gmail.com");
                employeeRepo.save(e);

                User rh = new User();
                rh.setEmail("rh@gmail.com");
                rh.setMDP("rh123");
                rh.setRole("RH");
                userRepo.save(rh);

                User u = new User();
                u.setEmail("employee@gmail.com");
                u.setMDP("emp123");
                u.setRole("EMPLOYE");
                u.setEmployee(e);
                userRepo.save(u);
            }
        };
    }
}