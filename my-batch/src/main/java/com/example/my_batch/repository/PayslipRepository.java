package com.example.my_batch.repository;

import com.example.my_batch.model.Payslip;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PayslipRepository extends JpaRepository<Payslip, Long> {}
