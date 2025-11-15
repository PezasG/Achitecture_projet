package com.example.myapi.controller;

import com.example.myapi.model.User;
import com.example.myapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/authentificator")
public class AuthentificatorController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/login")
    public User login(@RequestParam String email, @RequestParam String mdp) {
        Optional<User> user = userRepository.findByEmailAndMDP(email, mdp);
        return user.orElse(null);
    }
}
