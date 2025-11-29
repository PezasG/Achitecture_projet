package com.example.myapi.controller;

import com.example.myapi.model.Employee;
import com.example.myapi.service.EmployeeService;
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

    // Avoir tous les employés
    @GetMapping("/all")
    public ResponseEntity<List<Employee>> getAllEmployees() {
        List<Employee> employees = employeeService.getAllEmployees();
        return ResponseEntity.ok(
                employees == null ? Collections.emptyList() : employees
        );
    }

    // Avoir un employé par son ID
    @GetMapping("/get/{id}")
    public ResponseEntity<Employee> getEmployee(@PathVariable Long id) {
        return ResponseEntity.ok(employeeService.getEmployeeById(id));
    }

    // Ajouter un employé
    @PostMapping("/save")
    public ResponseEntity<Employee> createEmployee(@RequestBody Employee employee) {

        // abilities doit être RH ou EMPLOYE
        if (employee.getAbilities() == null ||
                (!employee.getAbilities().equalsIgnoreCase("RH")
                        && !employee.getAbilities().equalsIgnoreCase("EMPLOYE"))) {
            return ResponseEntity.badRequest().body(null);
        }

        Employee created = employeeService.createEmployee(employee);
        return ResponseEntity.ok(created);
    }

    // Modifier un employé
    @PutMapping("/update/{id}")
    public ResponseEntity<Employee> updateEmployee(@PathVariable Long id,
                                                   @RequestBody Employee employee) {
        return ResponseEntity.ok(employeeService.updateEmployee(id, employee));
    }

    // Supprimer un employé
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }
}
