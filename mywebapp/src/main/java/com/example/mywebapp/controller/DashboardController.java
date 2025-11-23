package com.example.mywebapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @GetMapping("/dashboard_employe")
    public String showLoginPage() {
        return "dashboard_employe"; // correspond à templates/login.html
    }
}