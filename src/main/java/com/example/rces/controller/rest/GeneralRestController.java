package com.example.rces.controller.rest;

import com.example.rces.dto.*;
import com.example.rces.models.Employee;
import com.example.rces.models.enums.GeneralReason;
import com.example.rces.models.enums.Item;
import com.example.rces.models.enums.Status;
import com.example.rces.payload.ReasonPayload;
import com.example.rces.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.example.rces.service.impl.CustomUserDetailsServiceImpl.currentUser;

@RestController
@RequestMapping("/api")
public class GeneralRestController {

    private final InconsistenciesService inconsistenciesService;
    private final CustomerOrderService customerOrderService;
    private final EmployeeService employeeService;
    private final SubDivisionService subDivisionService;
    private final ImageService imageService;

    @Autowired
    public GeneralRestController(CustomerOrderService customerOrderService, EmployeeService employeeService, InconsistenciesService inconsistenciesService, SubDivisionService subDivisionService, ImageService imageService) {
        this.customerOrderService = customerOrderService;
        this.employeeService = employeeService;
        this.inconsistenciesService = inconsistenciesService;
        this.subDivisionService = subDivisionService;
        this.imageService = imageService;
    }

    @GetMapping("/employees")
    public List<EmployeeDTO> getEmployees(@RequestParam(required = false) Object param) {
        if (param != null) {
            return employeeService.findAllByRole((String) param);
        } else {
            return employeeService.findAll();
        }
    }

    @GetMapping("/updater")
    @ResponseBody
    public Employee getUpdater() {
        return currentUser().orElseThrow();
    }

    @GetMapping("/customer-orders")
    public List<CustomerOrderDTO> getCustomerOrders() {
        return customerOrderService.findAllPayload();
    }

    @GetMapping("/reasons")
    public List<ReasonPayload> getReasons(@RequestParam String param) {
        return Arrays.stream(GeneralReason.values())
                .filter(reason -> reason.getRequestType().equals(param))
                .map(reason -> new ReasonPayload(reason.getName()))
                .collect(Collectors.toList());
    }

    @GetMapping("/inconsistency")
    public List<InconsistencyDto> getInconsistency(@RequestParam String param) {
        return inconsistenciesService.findAllByType(param);
    }

    @GetMapping("/item")
    public List<ItemDto> getItems() {
        return Arrays.stream(Item.values()).map(item -> new ItemDto(item.getName())).toList();
    }

    @GetMapping("/status")
    public List<Status> getStatus(@RequestParam String param) {
        return switch (param) {
            case "ADMIN" -> Arrays.asList(Status.values());
            case "OTK", "CONSTRUCTOR", "TECHNOLOGIST" -> Arrays.asList(Status.InWork, Status.Completed);
            case "MASTER" -> Arrays.asList(Status.Closed, Status.Cancel);
            default ->  Collections.emptyList();
        };
    }

    @GetMapping("/sub-divisions")
    public List<SubDivisionDTO> getSubDivisions() {
        return subDivisionService.getAll();
    }

    @DeleteMapping("/delete-image/{id}")
    public ResponseEntity<Void> deleteImage(@PathVariable UUID id) {
        imageService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
