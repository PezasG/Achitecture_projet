package com.example.myapi.controller;

import com.example.myapi.model.Employee;
import com.example.myapi.service.EmployeeService;
import com.example.myapi.DTO.EmployeeCreateDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    // Avoir tous les employés
    @GetMapping("/all")
    public ResponseEntity<List<Employee>> getAllEmployees() {
        List<Employee> employees = employeeService.getAllEmployees();
        return ResponseEntity.ok(employees == null ? Collections.emptyList() : employees);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<Employee> getEmployee(@PathVariable Long id) {
        Employee employee = employeeService.getEmployeeById(id);
        return ResponseEntity.ok(employee);
    }

    // @PostMapping("/save")
    //    public ResponseEntity<Employee> createEmployee(@RequestBody Employee employee) {
    //        Employee created = employeeService.createEmployee(employee);
    //        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    //    }

    // Ajouter un employé
    @PostMapping("/employees")
    public ResponseEntity<Employee> createEmployee(@RequestBody Employee employee, @RequestParam String role) {
        String finalRole = role.toUpperCase();
        if (!finalRole.equals("RH") && !finalRole.equals("EMPLOYE")) {
            return ResponseEntity.badRequest().build();
        }
        Employee created = employeeService.createEmployee(employee, finalRole);
        return ResponseEntity.ok(created);
    }

    // Modifier un employé
    @PutMapping("/update/{id}")
    public ResponseEntity<Employee> updateEmployee(@PathVariable Long id, @RequestBody Employee employee) {
        Employee updated = employeeService.updateEmployee(id, employee);
        return ResponseEntity.ok(updated);
    }

    // Supprimer un employé
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }

}