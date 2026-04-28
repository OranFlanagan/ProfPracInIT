package com.TicketingApp.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;
import com.TicketingApp.Repository.ProductRepository;
import com.TicketingApp.Repository.IssuesRepository;
import com.TicketingApp.Repository.IssueFixRepository;
import com.TicketingApp.Entity.Product;
import com.TicketingApp.Entity.Issue;
import com.TicketingApp.Entity.IssueFix;
import jakarta.transaction.Transactional;
import java.util.*;

@Controller
public class AdminDeflectionController {

    private final ProductRepository productRepository;
    private final IssuesRepository issuesRepository;
    private final IssueFixRepository issueFixRepository;

    public AdminDeflectionController(
        ProductRepository productRepository,
        IssuesRepository issuesRepository,
        IssueFixRepository issueFixRepository
    ) {
        this.productRepository = productRepository;
        this.issuesRepository = issuesRepository;
        this.issueFixRepository = issueFixRepository;
    }

    @GetMapping("/admin-deflection-editor")
    public String getAdminDeflectionPage(Model model) {
        model.addAttribute("products", productRepository.findAllWithIssues());
        return "admin-deflection-editor";
    }

    @PostMapping("/admin-deflection-editor/add-product")
    public String addProduct(
        @RequestParam String name,
        @RequestParam String category,
        @RequestParam String urlString,
        @RequestParam(defaultValue = "false") boolean featuredOnSupportPage
    ) {
        Product p = new Product();
        p.setName(name);
        p.setCategory(category);
        p.setUrlString(urlString);
        p.setFeaturedOnSupportPage(featuredOnSupportPage);
        productRepository.save(p);
        return "redirect:/admin-deflection-editor";
    }

    @PostMapping("/admin-deflection-editor/add-issue")
    public String addIssue(
        @RequestParam String title,
        @RequestParam Long productId
    ) {
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) return "redirect:/admin-deflection-editor";
        Issue issue = new Issue();
        issue.setTitle(title);
        issue.setProduct(product);
        issuesRepository.save(issue);
        return "redirect:/admin-deflection-editor";
    }

    @PostMapping("/admin-deflection-editor/add-fix")
    public String addFix(
        @RequestParam String fixDescription,
        @RequestParam Long issueId
    ) {
        Issue issue = issuesRepository.findById(issueId).orElse(null);
        if (issue == null) return "redirect:/admin-deflection-editor";
        IssueFix fix = new IssueFix();
        fix.setFixDescription(fixDescription);
        fix.setIssue(issue);
        issueFixRepository.save(fix);
        return "redirect:/admin-deflection-editor";
    }

    @PostMapping("/admin-deflection-editor/remove-product")
    public String removeProduct(@RequestParam Long productId) {
        productRepository.deleteById(productId);
        return "redirect:/admin-deflection-editor";
    }

    @PostMapping("/admin-deflection-editor/remove-issue")
    public String removeIssue(@RequestParam Long issueId) {
        issueFixRepository.deleteAll(issueFixRepository.findByIssue_id(issueId));
        issuesRepository.deleteById(issueId);
        return "redirect:/admin-deflection-editor";
    }
// search
@GetMapping("/admin-deflection-editor/search")
public String searchProducts(@RequestParam(required = false, defaultValue = "") String search,
                              Model model) {
    List<Product> products = productRepository.findAllWithIssues();
    if (!search.isBlank()) {
        String lower = search.toLowerCase();
        products = products.stream()
            .filter(p -> p.getName().toLowerCase().contains(lower))
            .collect(java.util.stream.Collectors.toList());
    }
    model.addAttribute("products", products);
    return "fragments/admin-product-rows :: rows";
}

// autocomplete
@GetMapping("/admin-deflection-editor/autocomplete")
public String autocomplete(@RequestParam(required = false, defaultValue = "") String search,
                           Model model) {
    if (search.isBlank()) return "fragments/admin-autocomplete :: empty";
    List<Product> suggestions = productRepository
        .findByNameStartingWithIgnoreCase(search)
        .stream()
        .limit(6)
        .collect(java.util.stream.Collectors.toList());
    model.addAttribute("suggestions", suggestions);
    return "fragments/admin-autocomplete :: suggestions";
}

// suggestion click — reuses same fragment as search
@GetMapping("/admin-deflection-editor/suggestion")
public String selectSuggestion(@RequestParam(defaultValue = "") String search, Model model) {
    List<Product> products = productRepository.findAllWithIssues();
    if (!search.isBlank()) {
        String lower = search.toLowerCase();
        products = products.stream()
            .filter(p -> p.getName().toLowerCase().contains(lower))
            .collect(java.util.stream.Collectors.toList());
    }
    model.addAttribute("products", products);
    return "fragments/admin-product-rows :: rows";
    }
}