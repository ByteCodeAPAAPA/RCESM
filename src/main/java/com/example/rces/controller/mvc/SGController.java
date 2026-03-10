package com.example.rces.controller.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/sgi")
public class SGController {

    @GetMapping
    public String getSGIForm() {
        return "sgi";
    }

}
