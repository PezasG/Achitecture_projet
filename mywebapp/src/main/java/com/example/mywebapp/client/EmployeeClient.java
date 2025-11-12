package com.example.mywebapp.client;

import com.example.mywebapp.model.Employee;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "employeeClient", url = "http://localhost:8080")
public interface EmployeeClient {
    @GetMapping("/api/employees")
    List<Employee> getAllEmployees();

    // CREATE
    @PostMapping("/api/employees/save")
    Employee save(Employee employee);

    // DELETE
    @DeleteMapping("/api/employees/delete/{id}")
    Employee delete(@PathVariable Long id);

    // UPDATE
    @PutMapping("/api/employees/update/{id}")
    Employee update(@PathVariable Long id, @RequestBody Employee employee);
}
