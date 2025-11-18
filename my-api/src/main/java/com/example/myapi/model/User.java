package com.example.myapi.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String mdp;
    private String role;

    @OneToOne // dit que c'est une relation unique
    @JoinColumn(name = "ID_employee", referencedColumnName = "id") // ajoute l'id de l'employer concerné à la db (foreing key)
    @JsonManagedReference
    private Employee employee;

    public User() {}

    public User(String email, String mdp, String role, Employee employee) {

        this.email = email;
        this.mdp = mdp;
        this.role = role;
        this.employee = employee;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMDP() {
        return mdp;
    }

    public void setMDP(String mdp) {
        this.mdp = mdp;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }
}
