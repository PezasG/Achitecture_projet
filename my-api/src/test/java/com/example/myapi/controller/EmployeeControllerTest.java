package com.example.myapi.controller;

import com.example.myapi.model.Employee;
import com.example.myapi.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmployeeController.class)
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EmployeeService employeeService;

    // ✅ Nouvelle façon de définir ton mock dans le contexte Spring
    @TestConfiguration
    static class MockConfig {
        @Bean
        EmployeeService employeeService() {
            return Mockito.mock(EmployeeService.class);
        }
    }

    @Test
    void testGetAllEmployees() throws Exception {
        List<Employee> mockEmployees = Arrays.asList(
                new Employee("Gaëtan", "Pezas", "gaetan.pezas@test.fr", "Dev", "123", 35, 11.0, "EMPLOYE"),
                new Employee("Yacine", "Znedi", "yacine.znedi@test.fr", "Dev", "456", 35, 22.0, "EMPLOYE"));

        when(employeeService.getAllEmployees()).thenReturn(mockEmployees);

        mockMvc.perform(get("/api/employees")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                // 💡 Correction : tu testes la taille → ici elle devrait être 2
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].firstName").isNotEmpty())
                .andExpect(jsonPath("$[0].lastName").isNotEmpty())
                .andExpect(jsonPath("$[0].email").isNotEmpty())
                .andExpect(jsonPath("$[0].job").isNotEmpty())
                .andExpect(jsonPath("$[0].hours").isNotEmpty())
                .andExpect(jsonPath("$[0].salary").isNotEmpty());
    }
}