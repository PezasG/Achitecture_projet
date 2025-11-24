package com.example.mywebapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.ui.Model;
import java.util.Map;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String handleLogin(@RequestParam String username,
                              @RequestParam String password,
                              Model model) {

        RestTemplate restTemplate = new RestTemplate();

        // Appel API pour vérifier l’utilisateur
        String apiUrl = "http://localhost:8080/api/authentificator/login";

        try {
            Map response = restTemplate.postForObject(
                    apiUrl,
                    Map.of("email", username, "mdp", password),
                    Map.class
            );

            if (response == null || !response.containsKey("role")) {
                model.addAttribute("error", "Identifiants incorrects");
                return "login";
            }

            String role = (String) response.get("role");

            if ("RH".equals(role)) {
                return "redirect:/employee_list";
            } else if ("EMPLOYE".equals(role)) {
                return "redirect:/dashboard_employe";
            } else {
                model.addAttribute("error", "Rôle inconnu : " + role);
                return "login";
            }

        } catch (Exception e) {
            model.addAttribute("error", "Erreur serveur : " + e.getMessage());
            return "login";
        }
    }
}
