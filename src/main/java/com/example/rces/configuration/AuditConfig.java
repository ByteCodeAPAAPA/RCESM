package com.example.rces.configuration;

import com.example.rces.models.Employee;
import com.example.rces.service.EmployeeService;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;

import static com.example.rces.service.impl.CustomUserDetailsServiceImpl.currentUser;

@Configuration
@EnableJpaAuditing
public class AuditConfig {

    @Bean
    public AuditorAware<Employee> auditorAware(EmployeeService employeeService) {
        return () -> {
            try {
                return currentUser();
            } catch (Exception e) {
                return Optional.empty();
            }
        };
    }

    @Bean
    public AuditReader auditReader(EntityManagerFactory entityManagerFactory) {
        return AuditReaderFactory.get(entityManagerFactory.createEntityManager());
    }
}
