package com.TicketingApp.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.TicketingApp.Repository.*;
import com.TicketingApp.Entity.*;
import java.util.*;

@Controller
public class CommonIssuesController {

    private final IssuesRepository issuesRepository;
    private final ProductRepository productRepository;

    public CommonIssuesController(IssuesRepository issuesRepository, 
                                  ProductRepository productRepository) {
        this.issuesRepository = issuesRepository;
        this.productRepository = productRepository;
    }

    @GetMapping("/common-issues/{productId}")
    public String getCommonIssuesPage(@PathVariable long productId, Model model) {

        List<Issue> issues = issuesRepository.findByProduct_Id(productId);        

        Product product = productRepository.findById(productId).orElse(null);

        model.addAttribute("issues", issues);
        model.addAttribute("product", product);

        return "common-issues";
    }
}