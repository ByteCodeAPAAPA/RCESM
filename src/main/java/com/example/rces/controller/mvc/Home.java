package com.example.rces.controller.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class Home {

    @GetMapping
    public String home() {
        return "redirect:/menu";
    }

    @GetMapping("/createBid")
    public String create() {
        return "home";
    }

    @GetMapping("mobiledevice")
    public String errorMobile() {
        return "mobiledevice";
    }

}
