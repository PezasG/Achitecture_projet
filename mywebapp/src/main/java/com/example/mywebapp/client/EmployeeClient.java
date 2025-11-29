package com.example.mywebapp.client;

import com.example.mywebapp.model.Employee;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "employee-api", url = "http://localhost:8080/api/employees")
public interface EmployeeClient {

    @GetMapping("/all")
    List<Employee> getAllEmployees();

    @PostMapping("/save")
    void save(@RequestBody Employee employee);

    @PutMapping("/update/{id}")
    void update(@PathVariable("id") Long id, @RequestBody Employee employee);

    @DeleteMapping("/delete/{id}")
    void delete(@PathVariable("id") Long id);
}
