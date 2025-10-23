package com.example.mywebapp.client;

import com.example.mywebapp.model.Employee;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@FeignClient(name = "employeeClient", url = "http://localhost:8080")
public interface EmployeeClient {
    @GetMapping("/api/employees")
    List<Employee> getAllEmployees();

    @PostMapping("/api/employees/save")
    Employee save(Employee employee);

    @DeleteMapping("/api/employees/delete/{id}")
    Employee delete(@PathVariable Long id);

}
