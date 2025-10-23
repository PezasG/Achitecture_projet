package com.example.myapi.controller;

import com.example.myapi.model.Employee;
import com.example.myapi.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmployeeController.class)
public class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeService employeeService;

    @Test
    public void testGetAllEmployees() throws Exception {
        // Création de données fictives
        List<Employee> mockEmployees = Arrays.asList(
                new Employee("Gaëtan", "Pezas", "gaetan.pezas@test.fr", "Dev")
        );

        // Simulation du comportement du service
        when(employeeService.getAllEmployees()).thenReturn(mockEmployees);

        // Appel de la méthode du contrôleur et vérifications
        mockMvc.perform(get("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // HTTP 200 OK
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1)) // 2 employés
                .andExpect(jsonPath("$[0].name").value("Alice")) // Premier nom
                .andExpect(jsonPath("$[1].name").value("Bob")); // Deuxième nom
    }
}
