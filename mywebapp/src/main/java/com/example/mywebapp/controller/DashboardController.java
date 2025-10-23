package com.example.mywebapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    // Quand tu vas sur http://localhost:8081/login
    // → cela affiche le fichier templates/login.html
    @GetMapping("/dashboard_employe")
    public String showLoginPage() {
        return "dashboard_employe"; // correspond à templates/login.html
    }
}