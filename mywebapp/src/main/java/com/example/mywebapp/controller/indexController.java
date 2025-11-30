package com.example.mywebapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class indexController {

    // Quand tu vas sur http://localhost:8081/login
    // → cela affiche le fichier templates/login.html
    @GetMapping("/")
    public String showIndexPage() {
        return "index";
    }
}