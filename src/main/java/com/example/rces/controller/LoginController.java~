package com.example.rces.controller;

import com.example.rces.configuration.CustomAuthenticationProvider;
import com.example.rces.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    private final CustomAuthenticationProvider customAuthenticationProvider;
    private final EmployeeService employeeService;

    @Autowired
    public LoginController(CustomAuthenticationProvider customAuthenticationProvider, EmployeeService employeeService) {
        this.customAuthenticationProvider = customAuthenticationProvider;
        this.employeeService = employeeService;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/perform-login")
    public String login(@RequestParam String username, Model model) {
        try {
            UserDetails user = employeeService.loadUserByUsername(username);
            UsernamePasswordAuthenticationToken authRequest =
                    new UsernamePasswordAuthenticationToken(username, null, user.getAuthorities());
            Authentication authentication = customAuthenticationProvider.authenticate(authRequest);
            if (authentication.isAuthenticated()) {
                SecurityContextHolder.getContext().setAuthentication(authentication);
                return "registration";
            }
        } catch (AuthenticationException e) {
            model.addAttribute("error", "Ошибка аутентификации: " + e.getMessage());
        }
        return "login";
    }
}
