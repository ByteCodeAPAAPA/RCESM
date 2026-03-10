package com.example.rces.controller.mvc;

import com.example.rces.dto.EmployeeDTO;
import com.example.rces.dto.SubDivisionDTO;
import com.example.rces.models.Employee;
import com.example.rces.models.Requests;
import com.example.rces.models.enums.Role;
import com.example.rces.models.enums.Status;
import com.example.rces.service.EmployeeService;
import com.example.rces.service.RequestsService;
import com.example.rces.service.SubDivisionService;
import com.example.rces.utils.DecimalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static com.example.rces.service.impl.CustomUserDetailsServiceImpl.currentUser;
import static com.example.rces.utils.DateUtil.formatedDate;
import static com.example.rces.utils.DecimalUtil.*;
import static com.example.rces.utils.ServiceUtil.getCreateRequestsMaster;

@Controller
public class RegistrationsController {

    private final EmployeeService employeeService;
    private final RequestsService requestsService;
    private final SubDivisionService subDivisionService;

    @Autowired
    public RegistrationsController(EmployeeService employeeService, RequestsService requestsService, SubDivisionService subDivisionService) {
        this.employeeService = employeeService;
        this.requestsService = requestsService;
        this.subDivisionService = subDivisionService;
    }

    @GetMapping("/admin")
    public String admin(@AuthenticationPrincipal Employee currentUser, Model model) {
        List<Role> roles = List.of(Role.values());
        List<SubDivisionDTO> mlmNodes = subDivisionService.getAll();
        List<EmployeeDTO> employees = employeeService.findAll();
        model.addAttribute("users", employees);
        model.addAttribute("user", currentUser);
        model.addAttribute("mlmNodes", mlmNodes);
        model.addAttribute("roles", roles);
        return "admin";
    }

    @GetMapping("/menu")
    public String menu(Model model) {
        List<Requests> requests = requestsService.findAll();
        Employee user = currentUser().orElseThrow();
        EmployeeDTO userDTO = employeeService.getCurrentUserDTO();
        List<Requests> requestsList;
        if (user.getRole().equalsIgnoreCase(String.valueOf(Role.MASTER))) {
            requestsList = requests.stream()
                    .filter(r -> r.getCreatedBy().equals(user))
                    .filter(r -> r.getStatus().equals(Status.New))
                    .sorted(Comparator.comparing(Requests::getRequestNumber))
                    .toList();
        } else {
            requestsList = requests.stream()
                    .filter(r -> r.getEmployee() != null)
                    .filter(r -> r.getEmployee().equals(user))
                    .filter(r -> r.getStatus().equals(Status.New))
                    .sorted(Comparator.comparing(Requests::getRequestNumber))
                    .toList();
        }
        Map<String, List<Requests>> createMasterRequest = getCreateRequestsMaster(requestsList);
        Map<String, List<Integer>> dailyCountsMap = getCountDays(requests);
        Map<String, Integer> qtyRequests = countRequest(requests);
        List<Requests> requestsFilterDate = filterRequestsByCurrentMonth(
                requests, LocalDate.now());
        List<Integer> dailyCountsList = countDailyRequestsList(requestsFilterDate);
        List<Requests> rejectedBid = requests.stream()
                .filter(req -> req.getSubDivision().equals(user.getSubDivision()))
                .filter(req -> req.getStatus().equals(Status.Rejected))
                .sorted(Comparator.comparing(Requests::getRequestNumber))
                .toList();
        List<String> rejectedDate = rejectedBid.stream()
                .map(req -> formatedDate(req.getUpdatedDate()))
                .toList();
        List<String> requestsDate = requestsList.stream()
                .map(req -> formatedDate(req.getCreatedDate()))
                .toList();

        int daysCount = dailyCountsList.size();
        List<String> chartDates = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (int i = daysCount - 1; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            chartDates.add(date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        }

        Map<String, List<List<Map<String, String>>>> allRequests =
                DecimalUtil.getAllRequestsByTypes(
                        requestsFilterDate,
                        chartDates,
                        "ОГК", "ОТК", "ОГТ"
                );

        model.addAttribute("user", userDTO);
        model.addAttribute("requests", requestsList);
        model.addAttribute("requestsMaster", createMasterRequest);
        model.addAttribute("dailyCounts", dailyCountsList);
        model.addAttribute("dailyCountsConstructor", dailyCountsMap.get("constructor"));
        model.addAttribute("dailyCountOtk", dailyCountsMap.get("otk"));
        model.addAttribute("dailyCountTechnologist", dailyCountsMap.get("technologist"));
        model.addAttribute("qtuRequests", qtyRequests.get("constructor"));
        model.addAttribute("qtuRequestsOtk", qtyRequests.get("otk"));
        model.addAttribute("rejectedBid", rejectedBid);
        model.addAttribute("rejectedDate", rejectedDate);
        model.addAttribute("requestsDate", requestsDate);
        model.addAttribute("chartDates", chartDates);
        model.addAttribute("dailyApplicationsConstructor", allRequests.get("ОГК"));
        model.addAttribute("dailyApplicationsOtk", allRequests.get("ОТК"));
        model.addAttribute("dailyApplicationsTechnologist", allRequests.get("ОГТ"));
        model.addAttribute("qtuRequestTechnologist", qtyRequests.get("technologist"));
        return "menu";
    }

    @GetMapping("/registration")
    public String registration(Model model) {
        model.addAttribute("userRole", Role.values());
        model.addAttribute("mlmNode", subDivisionService.getAll());
        return "registration";
    }

    @PostMapping("/registration")
    @Transactional
    public String addUser(@RequestParam String username,
                          @RequestParam String mlmNode,
                          @RequestParam String role,
                          @RequestParam String password,
                          @RequestParam Long chatId) {
        employeeService.save(username, mlmNode, role, password, chatId);
        return "redirect:/admin";
    }


    @PostMapping("/update")
    public String updateUser(@RequestParam Long id,
                             @RequestParam(required = false) String userName,
                             @RequestParam(required = false) String mlmNodeName,
                             @RequestParam(required = false) String roleName,
                             @RequestParam(required = false) Long chatName,
                             @RequestParam(required = false) Boolean active) {
        employeeService.update(id, userName, mlmNodeName, roleName, chatName, active);
        return "redirect:/admin";
    }

}