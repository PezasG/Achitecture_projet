package com.example.myapi.service;

import com.example.myapi.model.Employee;
import com.example.myapi.repository.EmployeeRepository;
import com.example.myapi.exeption.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.List;

@Service
public class EmployeeService {

    @Autowired
    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    // Renvoie la liste des employés
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    // Renvoie un employé trouvé par son ID
    public Employee getEmployeeById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id " + id));
    }

    // Creer un employé dans la base
    public Employee createEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    // Mise à jour des infos
    public Employee updateEmployee(Long id, Employee updated) {
        Employee existing = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id " + id));

        if (updated.getFirstName() != null)
            existing.setFirstName(updated.getFirstName());
        if (updated.getLastName() != null)
            existing.setLastName(updated.getLastName());
        if (updated.getEmail() != null)
            existing.setEmail(updated.getEmail());
        if (updated.getJob() != null)
            existing.setJob(updated.getJob());
        if (updated.getMdp() != null)
            existing.setMdp(updated.getMdp());
        if (updated.getHours() != 0)
            existing.setHours(updated.getHours());
        if (updated.getHourlyRate() != null)
            existing.setHourlyRate(updated.getHourlyRate());
        if (updated.getAbilities() != null)
            existing.setAbilities(updated.getAbilities());

        return employeeRepository.save(existing);
    }

    // Supprime un employé
    public void deleteEmployee(Long id) {
        Employee existing = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id " + id));
        employeeRepository.delete(existing);
    }
}