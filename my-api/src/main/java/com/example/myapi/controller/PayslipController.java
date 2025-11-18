package com.example.myapi.controller;

import com.example.myapi.model.Employee;
import com.example.myapi.model.Payslip;
import com.example.myapi.model.User;
import com.example.myapi.repository.EmployeeRepository;
import com.example.myapi.repository.PayslipRepository;
import com.example.myapi.repository.UserRepository;
import com.example.myapi.service.PayslipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payslips")
public class PayslipController {

    @Autowired
    private PayslipService payslipService;

    @Autowired
    private PayslipRepository payslipRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    // Avoir toutes les fiches de paies (pour RH)
    @GetMapping("/all")
    public List<Payslip> getAllPayslips() { return payslipService.getAllPayslips();}

    // Trouver une fiche de paie par id
    @GetMapping("/{id}")
    public ResponseEntity<Payslip> getById(@PathVariable Long id){ return ResponseEntity.ok(payslipService.getPayslipById(id));}

    // Trouver les fiches de paies d'un employé
    @GetMapping("/employee/{id}")
    public List<Payslip> getByEmployee(@PathVariable Long id){return payslipService.getPayslipsByEmployee(id);}


    // generer une fiche de paie ??
    @PostMapping("/generate/{month}")
    public void generate(@PathVariable String month){
        payslipService.generatePayslipsForMonth(month);
    }
}

