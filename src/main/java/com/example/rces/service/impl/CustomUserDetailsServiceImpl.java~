package com.example.rces.service.impl;

import com.example.rces.dto.EmployeeDTO;
import com.example.rces.mapper.EmployeeMapper;
import com.example.rces.models.Employee;
import com.example.rces.models.SubDivision;
import com.example.rces.models.enums.Role;
import com.example.rces.repository.EmployeeRepository;
import com.example.rces.service.EmployeeService;
import com.example.rces.service.SubDivisionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContextException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional(transactionManager = "primaryTransactionManager")
public class CustomUserDetailsServiceImpl implements UserDetailsService, EmployeeService {

    public final static Set<String> controlRoles = Set.of(
            Role.ADMIN.name(),
            Role.CONTROL.name()
    );

    private final EmployeeRepository repository;
    private final EmployeeMapper mapper;
    private final SubDivisionService subDivisionService;

    @Autowired
    public CustomUserDetailsServiceImpl(EmployeeRepository repository, EmployeeMapper mapper, SubDivisionService subDivisionService) {
        this.repository = repository;
        this.mapper = mapper;
        this.subDivisionService = subDivisionService;
    }

    @Override
    public void save(String username, String mlmNode, String role, String password, Long chatId) {
        Employee employee = new Employee();
        SubDivision subDivision = subDivisionService.getByName(mlmNode);
        employee.setName(username);
        employee.setSubDivision(subDivision);
        employee.setRole(role);
        employee.setPassword(password);
        employee.setChatId(chatId != -1 ? chatId : null);
        repository.save(employee);
    }

    @Override
    public void update(Long id, String userName, String mlmNodeName, String roleName, Long chatId, Boolean active) {
        Employee employee = repository.findById(id).orElseThrow(() -> new ApplicationContextException("Пользователь не найден"));
        SubDivision subDivision = subDivisionService.getByName(mlmNodeName);
        employee.setName(userName);
        employee.setSubDivision(subDivision);
        employee.setRole(roleName);
        employee.setChatId(chatId != -1 ? chatId : null);
        employee.setActive(active);
        repository.save(employee);
    }


    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    @Override
    public Employee loadUserByUsername(String name) {
        try {
            return repository.findByName(name);
        } catch (Exception e) {
            throw new UsernameNotFoundException("Пользователь не найден");
        }
    }

    @Override
    public List<EmployeeDTO> findAll() {
        return repository.findAll().stream().map(mapper::toDTO).toList();
    }

    @Override
    public List<EmployeeDTO> findAllByRole(String role) {
        return repository.findAllByRole(role).stream().map(mapper::toDTO).toList();
    }

    public static Optional<Employee> currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) return Optional.empty();
        Object principal = authentication.getPrincipal();
        return Optional.ofNullable((Employee) principal);
    }

    public EmployeeDTO getCurrentUserDTO() {
        return mapper.toDTO(currentUser().orElseThrow());
    }

    @Override
    public Boolean currentUserHaveControlRoles() {
        return controlRoles.contains(currentUser().orElseThrow().getRole());
    }

    @Override
    public Boolean isResponsible(Employee responsobleEmployee) {
        return responsobleEmployee.getId().equals(currentUser().orElseThrow().getId());
    }

    @Override
    public void setSecurityContext(Employee employee) {
        Authentication auth = new UsernamePasswordAuthenticationToken(
                employee,
                null,
                List.of(new SimpleGrantedAuthority(employee.getRole()))
        );
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);
    }

}