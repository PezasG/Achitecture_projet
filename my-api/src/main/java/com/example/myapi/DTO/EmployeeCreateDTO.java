package com.example.myapi.DTO;

import com.example.myapi.model.Employee;

public class EmployeeCreateDTO {
    private Employee employee;
    private String role;

    public Employee getEmployee() { return employee; }
    public String getRole() { return role; }
}

