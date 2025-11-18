package com.example.myapi.controller;

import com.example.myapi.model.User;
import com.example.myapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Optional;

@RestController
@RequestMapping("/api/authentificator")
@CrossOrigin
public class AuthentificatorController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/login")
    public HashMap<String, Object> login(@RequestBody HashMap<String, String> body) {

        String email = body.get("email");
        String mdp = body.get("password");

        Optional<User> user = userRepository.findByEmailAndMdp(email, mdp);

        HashMap<String, Object> response = new HashMap<>();

        if (user.isPresent()) {
            User u = user.get();
            response.put("status", "success");
            response.put("role", u.getRole());
            response.put("userId", u.getId());
            return response;
        } else {
            response.put("status", "error");
            return response;
        }
    }
}
