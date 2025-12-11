package com.example.my_batch.model;

import jakarta.persistence.*;

@Entity
public class Payslip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String month;
    private Double grossSalary;
    private Double netSalary;

    @Column(length = 1024)
    private String urlPdf;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    public Payslip() {}

    public Payslip(String month, Double grossSalary, Double netSalary,
                   String urlPdf, Employee employee) {

        this.month = month;
        this.grossSalary = grossSalary;
        this.netSalary = netSalary;
        this.urlPdf = urlPdf;
        this.employee = employee;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public Double getGrossSalary() {
        return grossSalary;
    }

    public void setGrossSalary(Double grossSalary) {
        this.grossSalary = grossSalary;
    }

    public Double getNetSalary() {
        return netSalary;
    }

    public void setNetSalary(Double netSalary) {
        this.netSalary = netSalary;
    }

    public String getUrlPdf() {
        return urlPdf;
    }

    public void setUrlPdf(String urlPdf) {
        this.urlPdf = urlPdf;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }
}
