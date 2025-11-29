package com.example.mywebapp.model;

public class Employee {

    private Long id;

    private String firstName;
    private String lastName;
    private String email;
    private String job;
    private String mdp;
    private int hours;
    private int salary;
    private String abilities;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getJob() { return job; }
    public void setJob(String job) { this.job = job; }

    public String getMdp() { return mdp; }
    public void setMdp(String mdp) { this.mdp = mdp; }

    public int getHours() { return hours; }
    public void setHours(int hours) { this.hours = hours; }

    public int getSalary() { return salary; }
    public void setSalary(int salary) { this.salary = salary; }

    public String getAbilities() { return abilities; }
    public void setAbilities(String abilities) { this.abilities = abilities; }
}
