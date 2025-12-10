package com.example.mywebapp.client;

import com.example.mywebapp.model.Payslip;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "payslip-api", url = "http://localhost:8080/api/payslips")
public interface PayslipClient {

    @GetMapping("/employee/{id}")
    List<Payslip> getByEmployee(@PathVariable Long id);

    @PostMapping("/generate/{id}")
    void generate(@PathVariable Long id);

    @DeleteMapping("/delete/{id}")
    void delete(@PathVariable Long id);
}
