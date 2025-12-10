package com.example.mywebapp.model;

public class Payslip {

    private Long id;
    private String month;
    private String urlPdf;
    private Double netSalary;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getMonth() { return month; }
    public void setMonth(String month) { this.month = month; }

    public String getUrlPdf() { return urlPdf; }
    public void setUrlPdf(String urlPdf) { this.urlPdf = urlPdf; }

    public Double getNetSalary() { return netSalary; }
    public void setNetSalary(Double netSalary) { this.netSalary = netSalary; }
}
