package com.example.rces.service;

import com.example.rces.dto.EmployeeDTO;
import com.example.rces.models.Employee;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

public interface EmployeeService {

    void save(String username, String mlmNode, String role, String password, Long chatId);

    void update(Long id, String userName, String mlmNodeName, String roleName, Long chatId, Boolean active);

    void deleteById(Long id);

    Employee loadUserByUsername(String name);

    Boolean currentUserHaveControlRoles();

    Boolean isResponsible(Employee responsobleEmployee);

    EmployeeDTO getCurrentUserDTO();

    List<EmployeeDTO> findAllByRole(String role);

    void setSecurityContext(Employee employee);

    List<EmployeeDTO> findAll();
}
