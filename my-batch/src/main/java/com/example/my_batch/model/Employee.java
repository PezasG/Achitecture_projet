package com.example.my_batch.model;

import jakarta.persistence.*;

@Entity
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String FirstName;
    private String LastName;
    private String email;
    private String job;
    private String mdp;
    private int hours;
    private int salary;
    private String abilities;

    // Constructeurs
    public Employee() {}

    public Employee(String FirstName, String LastName, String email, String job,
                    String mdp, int hours, int salary, String abilities) {

        this.FirstName = FirstName;
        this.LastName = LastName;
        this.email = email;
        this.job = job;
        this.mdp = mdp;
        this.hours = hours;
        this.salary = salary;
        this.abilities = abilities;
    }

    // Getters & setters
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

    public String getMdp() {
        return mdp;
    }

    public void setMdp(String mdp) {
        this.mdp = mdp;
    }

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public int getSalary() {
        return salary;
    }

    public void setSalary(int salary) {
        this.salary = salary;
    }

    public String getAbilities() {
        return abilities;
    }

    public void setAbilities(String abilities) {
        this.abilities = abilities;
    }
}
