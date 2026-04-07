package com.TicketingApp.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class supportPageController {


@RequestMapping("/support-page")
public String getSupportPage() {
    return "support-page";
}


}