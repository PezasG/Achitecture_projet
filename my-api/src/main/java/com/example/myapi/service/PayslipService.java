package com.example.myapi.service;

import com.example.myapi.model.Employee;
import com.example.myapi.model.Payslip;
import com.example.myapi.model.User;
import com.example.myapi.repository.EmployeeRepository;
import com.example.myapi.exeption.ResourceNotFoundException;
import com.example.myapi.repository.UserRepository;
import com.example.myapi.repository.PayslipRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.List;

@Service
public class PayslipService {

    @Autowired
    private EmployeeRepository employeeRepo;

    @Autowired
    private PayslipRepository payslipRepo;

    public Payslip generatePayslipForEmployee(Employee emp, String month) {

        // Hyper chelou à refaire !!!

        double hours = emp.getHours();
        double baseSalary = emp.getSalary();

        double totalSalary = baseSalary + (hours * 10); // exemple

        Payslip p = new Payslip();
        p.setEmployee(emp);
        p.setMonth(month);
        p.setBaseSalary(baseSalary);
        p.setHoursWorked(hours);
        p.setTotalSalary(totalSalary);

        return payslipRepo.save(p);
    }

    // Génération pour tous les salariés
    public void generatePayslipsForMonth(String month){
        List<Employee> all = employeeRepo.findAll();

        for(Employee emp : all){
            generatePayslipForEmployee(emp, month);
        }
    }

    // Donne toutes les fiches de paies (pour RH)
    public List<Payslip> getAllPayslips(){ return payslipRepo.findAll();}

    public Payslip getPayslipById(long id) {
        return payslipRepo.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Payslip not found with id " + id));
    }

    public List<Payslip> getPayslipsByEmployee(Long employeeId) {
        Employee employee = employeeRepo.findById(employeeId)
                .orElseThrow(()->new ResourceNotFoundException("Employee not found with id " + employeeId));
        return payslipRepo.findByEmployee(employee);
    }
}

