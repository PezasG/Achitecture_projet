package com.example.my_batch.service;

import com.example.my_batch.model.Employee;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PayrollApiServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private PayrollApiService payrollApiService;

    @Test
    public void testCreatePayslipForEmployee_Success() {
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("id", 101);
        responseBody.put("urlPdf", "/path/to/pdf");

        when(restTemplate.postForEntity(
                eq("http://localhost:8080/api/payslips/create"),
                any(HttpEntity.class),
                eq(Map.class))).thenReturn(new ResponseEntity<>(responseBody, HttpStatus.OK));

        PayrollApiService.PayslipDto result = payrollApiService.createPayslipForEmployee(1L, "2025-01");

        assertNotNull(result);
        assertEquals(101L, result.getId());
        assertEquals("/path/to/pdf", result.getUrlPdf());
        assertEquals("2025-01", result.getMonth());
    }

    @Test
    public void testDownloadPayslipPdfBytes_Success() {
        byte[] fakePdf = new byte[] { 1, 2, 3 };
        when(restTemplate.exchange(
                eq("http://localhost:8080/api/payslips/download/101"),
                eq(HttpMethod.GET),
                isNull(),
                eq(byte[].class))).thenReturn(new ResponseEntity<>(fakePdf, HttpStatus.OK));

        byte[] result = payrollApiService.downloadPayslipPdfBytes(101L);

        assertNotNull(result);
        assertArrayEquals(fakePdf, result);
    }
}
