package com.example.rces.controller.mvc;

import com.example.rces.dto.CreateRequestDto;
import com.example.rces.dto.RequestDto;
import com.example.rces.dto.RequestHistoryDTO;
import com.example.rces.dto.SubDivisionDTO;
import com.example.rces.mapper.SubDivisionMapper;
import com.example.rces.models.Employee;
import com.example.rces.models.Requests;
import com.example.rces.service.EmployeeService;
import com.example.rces.service.RequestHistoryService;
import com.example.rces.service.RequestsService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static com.example.rces.service.impl.CustomUserDetailsServiceImpl.currentUser;
import static com.example.rces.utils.DateUtil.formatedDate;


@Controller
public class RequestController {

    private final RequestsService requestsService;
    private final SubDivisionMapper subDivisionMapper;
    private final RequestHistoryService requestHistoryService;
    private final EmployeeService employeeService;

    @Autowired
    public RequestController(RequestsService requestsService,
                             SubDivisionMapper subDivisionMapper, RequestHistoryService requestHistoryService,
                             EmployeeService employeeService) {
        this.requestsService = requestsService;
        this.subDivisionMapper = subDivisionMapper;
        this.requestHistoryService = requestHistoryService;
        this.employeeService = employeeService;
    }

    @GetMapping("/create")
    public String getCreateBidForm(@RequestParam String type, Model model) {
        if (!Arrays.stream(Requests.Type.values()).map(Enum::name).toList().contains(type)) {
            model.addAttribute("type", type);
            return "error";
        }
        Employee currentUser = currentUser().orElseThrow();
        SubDivisionDTO subDivisionDTO = subDivisionMapper.toDTO(currentUser.getSubDivision());
        model.addAttribute("createForm", true);
        model.addAttribute("type", type);
        model.addAttribute(type, true);
        model.addAttribute("employeeName", currentUser.getName());
        model.addAttribute("mlmNodeEmployee", subDivisionDTO);
        return "/requests";
    }

    @PostMapping("/create")
    public String createRequest(@ModelAttribute CreateRequestDto createRequestDto, Model model,
                                @RequestParam("additionalFiles") MultipartFile[] additionalFiles) throws JsonProcessingException {

        Employee createdEmployee = currentUser().orElseThrow();
        RequestDto requestDto = requestsService.createRequest(createdEmployee, createRequestDto, additionalFiles);
        model.addAttribute("create", true);
        model.addAttribute("requestNumber", requestDto.getRequestNumber());
        return "success";
    }

    @GetMapping("/view/{requestNumber}")
    public String getViewBidForm(@PathVariable("requestNumber") Integer requestNumber, Model model) {
        Requests requests = requestsService.findByRequestNumber(requestNumber);
        Employee user = currentUser().orElseThrow();

        List<RequestHistoryDTO> requestHistoryDTOList = new ArrayList<>();
        if (requests.getQtyRejected() > 0) {
            requestHistoryDTOList = requestHistoryService.getRequestHistory(requests.getId());
        }

        model.addAttribute("bid", requests);
        model.addAttribute("type", requests.getTypeRequest());
        model.addAttribute("date", formatedDate(requests.getCreatedDate()));
        model.addAttribute("viewForm", true);
        model.addAttribute("requestHistoryDTOList", requestHistoryDTOList);
        model.addAttribute("employeeMaster", employeeService.findAllByRole("MASTER"));
        model.addAttribute("role", user.getRole());
        return "/requests";

    }

    @GetMapping("/requestslist/{type}")
    public String getRequestList(@PathVariable String type,
                                 Model model) {
        List<Requests> requestsList = requestsService.findAllByTypeRequest(Requests.Type.valueOf(type)).stream()
                .sorted(Comparator.comparing(Requests::getRequestNumber).reversed())
                .toList();
        List<String> formattedDates = requestsList.stream()
                .map(request -> formatedDate(request.getCreatedDate()))
                .toList();
        List<String> updateDate = requestsList.stream()
                .map(requests -> requests.getUpdatedDate().atZone(ZoneId.of("UTC")).format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")))
                .toList();

        model.addAttribute("requestsList", requestsList);
        model.addAttribute("typeRequest", type);
        model.addAttribute("formattedBidList", formattedDates);
        model.addAttribute("updateDateList", updateDate);
        return "requestslist";
    }

}