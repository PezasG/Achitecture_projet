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

        String apiUrl = "http://localhost:8080/api/authentificator/login";

        try {
            Map<String, Object> requestBody = Map.of(
                    "email", username,
                    "mdp", password
            );

            // On récupère un Employee complet
            Map response = restTemplate.postForObject(apiUrl, requestBody, Map.class);

            if (response == null || !response.containsKey("abilities")) {
                model.addAttribute("error", "Identifiants incorrects");
                return "login";
            }

            String role = (String) response.get("abilities");

            // Redirection selon abilities
            if ("RH".equalsIgnoreCase(role)) {
                return "redirect:/employee_list";
            } else if ("EMPLOYE".equalsIgnoreCase(role)) {
                return "redirect:/dashboard_employe";
            } else {
                model.addAttribute("error", "Rôle inconnu : " + role);
                return "login";
            }

        } catch (Exception e) {
            model.addAttribute("error", "Erreur serveur lors de la connexion.");
            return "login";
        }
    }
}
