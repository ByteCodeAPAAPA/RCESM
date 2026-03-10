package com.example.rces.controller.mvc;

import com.example.rces.service.TokenService;
import com.example.rces.spm.services.service.BProcessDocumentStepService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Controller
@RequestMapping("/shift-task")
public class ShiftTaskController {

    private final TokenService tokenService;
    private final BProcessDocumentStepService bProcessDocumentStepService;

    @Autowired
    public ShiftTaskController(TokenService tokenService, BProcessDocumentStepService bProcessDocumentStepService) {
        this.tokenService = tokenService;
        this.bProcessDocumentStepService = bProcessDocumentStepService;
    }

    @GetMapping("/task")
    public String task(Model model) throws IOException, InterruptedException {
        model.addAttribute("token", tokenService.getToken("api", "123456"));
        model.addAttribute("mlmNode", bProcessDocumentStepService.getMlmNodeList());
        model.addAttribute("valueTaskListSize", bProcessDocumentStepService.componentList().size());
        model.addAttribute("valueFinishedListSize", bProcessDocumentStepService.componentFinishedList().size());
        model.addAttribute("finished", bProcessDocumentStepService.componentFinishedList());
        model.addAttribute("taskList", bProcessDocumentStepService.componentList());
        model.addAttribute("dateStart", LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        return "task";
    }

}