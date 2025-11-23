package com.example.mywebapp.model;

import java.util.Date;

public class Employee {
    private Long id;

    private String FirstName;
    private String LastName;
    private String email;
    private String job;
    private int hours;
    private int salary;

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

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public int getHours() { return hours; }

    public void setHours(int hours) { this.hours = hours; }

    public int getSalary() { return salary; }

    public void setSalary(int salary) { this.salary = salary; }
}