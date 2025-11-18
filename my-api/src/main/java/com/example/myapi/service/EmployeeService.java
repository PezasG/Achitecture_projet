package com.example.myapi.service;

import com.example.myapi.model.Employee;
import com.example.myapi.model.User;
import com.example.myapi.repository.EmployeeRepository;
import com.example.myapi.exeption.ResourceNotFoundException;
import com.example.myapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.List;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Autowired
    private UserRepository userRepository;

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public Employee getEmployeeById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id " + id));
    }

    public Employee createEmployee(Employee employee, String role) {
        // Sauvegarde l'employé dans la base
        Employee saved = employeeRepository.save(employee);

        // Génération d'un mot de passe aléatoire
        String mdp = genererMdp(4);

        // Création du compte utilisateur lié à l'employé
        User user = new User();
        user.setEmail(employee.getEmail());
        user.setMDP(mdp);
        user.setRole(role.toUpperCase());
        user.setEmployee(saved);

        // Lier l'utilisateur à l'employé
        user.setEmployee(saved);

        // Sauvegarder le compte utilisateur
        userRepository.save(user);

        System.out.println("Compte créé pour " + employee.getEmail() + " — mot de passe : " + mdp);

        return saved;
    }

    public Employee updateEmployee(Long id, Employee updated) {
        Employee existing = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id " + id));

        // Mettre à jour les champs autorisés
        if (updated.getFirstName() != null) existing.setFirstName(updated.getFirstName());
        if (updated.getLastName() != null) existing.setLastName(updated.getLastName());
        if (updated.getEmail() != null) existing.setEmail(updated.getEmail());
        if (updated.getJob() != null) existing.setJob(updated.getJob());
        if (updated.getHours() != 0) existing.setHours(updated.getHours());
        if (updated.getSalary() != 0) existing.setSalary(updated.getSalary());

        return employeeRepository.save(existing);
    }

    public void deleteEmployee(Long id) {
        Employee existing = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id " + id));
        employeeRepository.delete(existing);
    }

    // --- Générateur de mot de passe ---
    private String genererMdp(int longueur) {
        // Caractères possibles dans le mot de passe
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        // Générateur de nombres aléatoires sécurisé
        SecureRandom random = new SecureRandom();

        // Builder pour construire le mot de passe
        StringBuilder sb = new StringBuilder(longueur);

        for (int i = 0; i < longueur; i++) {
            // Choisir un caractère aléatoire parmi chars
            int index = random.nextInt(chars.length());
            sb.append(chars.charAt(index));
        }

        return sb.toString(); // retourne le mot de passe en clair
    }

}