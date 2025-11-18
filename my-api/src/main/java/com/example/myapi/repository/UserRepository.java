package com.example.myapi.repository;

import com.example.myapi.model.User;
import com.example.myapi.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
    User findByEmployee(Employee employee);
    Optional<User> findByEmailAndMdp(String email, String mdp);
}
