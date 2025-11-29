package com.example.myapi.controller;

import com.example.myapi.model.Employee;
import com.example.myapi.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/authentificator")
public class AuthentificatorController {

    @Autowired
    private EmployeeRepository employeeRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {

        try {

            String email = body.get("email");
            String mdp = body.get("mdp");

            System.out.println("EMAIL RECU = " + email);
            System.out.println("MDP RECU = " + mdp);

            if (email == null || mdp == null) {
                return ResponseEntity.badRequest().body(
                        Map.of("status", "error", "message", "email et mdp sont obligatoires")
                );
            }

            Employee employee = employeeRepository.findByEmail(email);

            // Email introuvable
            if (employee == null) {
                return ResponseEntity.status(401).body(
                        Map.of("status", "error", "message", "Email incorrect")
                );
            }

            if (employee.getMdp() == null) {
                return ResponseEntity.status(500).body(
                        Map.of("status", "error", "message", "MDP vide dans la base !")
                );
            }

            // Mot de passe incorrect
            if (!employee.getMdp().equals(mdp)) {
                return ResponseEntity.status(401).body(
                        Map.of("status", "error", "message", "Mot de passe incorrect")
                );
            }

            // On vérifie abilities
            String abilities = employee.getAbilities();

            return ResponseEntity.ok(
                    Map.of(
                            "status", "success",
                            "role", abilities,
                            "employeeId", employee.getId()
                    )
            );

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(
                    Map.of("status", "error", "message", "Erreur interne : " + e.getMessage())
            );
        }
    }
}
