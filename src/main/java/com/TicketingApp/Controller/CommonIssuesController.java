package com.TicketingApp.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.GetMapping;


import com.TicketingApp.Repository.*;
import java.util.*;
import com.TicketingApp.Entity.*;

@Controller
public class CommonIssuesController {

    private final IssuesRepository issuesRepository;

    public CommonIssuesController(IssuesRepository issuesRepository) {
        this.issuesRepository = issuesRepository;
    }

    @GetMapping("/common-issues/{productId}")
    public String getCommonIssuesPage(@PathVariable int productId, Model model) {

        List<Issue> issues = issuesRepository.findByProductId(productId);

        model.addAttribute("issues", issues);

        return "common-issues";
    }
}
