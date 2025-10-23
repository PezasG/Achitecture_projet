package com.example.mywebapp.controller;

import com.example.mywebapp.client.EmployeeClient;
import com.example.mywebapp.model.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class EmployeeListeController {
    @Autowired
    private EmployeeClient employeeClient;

    @GetMapping("/employee_list")
    public String getEmployee(Model model) {
        List<Employee> employees = employeeClient.getAllEmployees();

        model.addAttribute("employees", employees);
        model.addAttribute("employee", new Employee());
        model.addAttribute("deleteEmployee", new Employee());
        return "employee_list";
    }

    @PostMapping("/add")
    public String saveEmployee(@ModelAttribute Employee employee) {
        employeeClient.save(employee);
        return "redirect:/employees";
    }

    @PostMapping("/delete/{id}")
    public String deleteEmployee(@ModelAttribute("delete") Employee employee) {
        System.out.println("ID reçu pour suppression : " + employee.getId());
        if (employee.getId() != null) {
            employeeClient.delete(employee.getId());
        }
        return "redirect:/employee_list";
    }
}
