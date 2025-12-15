package com.example.my_batch.model;

public class BatchResult {
    private final Employee employee;
    private final Long payslipId;
    private final String month;
    private final byte[] pdfBytes;
    private final String filename;

    public BatchResult(Employee employee, Long payslipId, String month, byte[] pdfBytes, String filename) {
        this.employee = employee;
        this.payslipId = payslipId;
        this.month = month;
        this.pdfBytes = pdfBytes;
        this.filename = filename;
    }

    public Employee getEmployee() { return employee; }
    public Long getPayslipId() { return payslipId; }
    public String getMonth() { return month; }
    public byte[] getPdfBytes() { return pdfBytes; }
    public String getFilename() { return filename; }
}
