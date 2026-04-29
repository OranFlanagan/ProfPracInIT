package com.TicketingApp.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DemoController {

    @GetMapping("/")
    public String root() {
        return "redirect:/demo";
    }

    @GetMapping("/demo")
    public String demo() {
        return "demo";
    }
}