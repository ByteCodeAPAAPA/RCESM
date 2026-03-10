package com.example.rces.controller.rest;

import com.example.rces.configuration.JwtService;
import com.example.rces.dto.EmployeeDTO;
import com.example.rces.models.Employee;
import com.example.rces.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final EmployeeRepository employeeRepository;
    private final JwtService jwtService;

    @Autowired
    public AuthenticationController(EmployeeRepository employeeRepository, JwtService jwtService) {
        this.employeeRepository = employeeRepository;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody EmployeeDTO employeeDTO) {
        Employee employee = employeeRepository.findByName(employeeDTO.getName());
        String jwtToken = jwtService.generateToken(employee);
        return ResponseEntity.ok(jwtToken);
    }

}
