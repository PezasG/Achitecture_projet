package com.example.myapi.model;

import jakarta.persistence.*;

import java.util.Date;

@Entity
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String FirstName;
    private String LastName;
    private String email;
    private String job;

    // Constructeurs
    public Employee() {}

    public Employee(String FirstName, String LastName, String email,  String job) {
        this.FirstName = FirstName;
        this.LastName = LastName;
        this.email = email;
        this.job = job;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getJob() { return job; }

    public void setJob(String job) { this.job = job; }
}