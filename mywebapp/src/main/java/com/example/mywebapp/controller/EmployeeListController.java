package com.example.mywebapp.controller;

import com.example.mywebapp.client.EmployeeClient;
import com.example.mywebapp.model.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class EmployeeListController {

    @Autowired
    private EmployeeClient employeeClient;

    @GetMapping("/employee_list")
    public String listEmployees(Model model) {
        List<Employee> employees = employeeClient.getAllEmployees();
        model.addAttribute("employees", employees);
        return "employee_list";
    }

    // CREATE (remains POST)
    @PostMapping("/employees/save")
    @ResponseBody
    public void saveEmployee(@RequestBody Employee employee) {
        employeeClient.save(employee);
    }

    // UPDATE -> handle PUT from JS, call Feign PUT
    @PutMapping("/employees/update/{id}")
    @ResponseBody
    public void updateEmployee(@PathVariable Long id, @RequestBody Employee employee) {
        employeeClient.update(id, employee);
    }

    // DELETE -> handle DELETE from JS, call Feign DELETE
    @DeleteMapping("/employees/delete/{id}")
    @ResponseBody
    public void deleteEmployee(@PathVariable Long id) {
        employeeClient.delete(id);
    }
}
