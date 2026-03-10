package com.example.rces.controller.mvc;

import com.example.rces.dto.ApplicationInfoDTO;
import com.example.rces.service.HelpService;
import com.example.rces.service.impl.HelpServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/help")
public class HelpController {

    private final HelpService helpService;

    @Autowired
    public HelpController(HelpServiceImpl helpService) {
        this.helpService = helpService;
    }

    @GetMapping
    public String showHelpPage(Model model) {
        ApplicationInfoDTO appInfo = helpService.getApplicationInfo();
        model.addAttribute("appInfo", appInfo);
        return "application-info";
    }

}
