package com.example.rces.repository;

import com.example.rces.models.Employee;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepository extends BaseAuditingRepository<Employee, Long> {

    @EntityGraph(attributePaths = {"subDivision"})
    List<Employee> findAllByRole(String role);

    @EntityGraph(attributePaths = {"subDivision"})
    Employee findByName(String name);
}
