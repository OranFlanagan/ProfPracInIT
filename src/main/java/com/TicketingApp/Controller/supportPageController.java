package com.TicketingApp.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.ui.Model;

import com.TicketingApp.Repository.ProductRepository;
import com.TicketingApp.Entity.Product;
import java.util.*;

@Controller
public class supportPageController {


@Autowired
private ProductRepository productRepository;

//regular supprt page 
@GetMapping("/support-page")
public String getSupportPage(@RequestParam(required = false, defaultValue = "") String search, 
                              Model model) {

    List<Product> products = search.isBlank()
        ? productRepository.findByFeaturedOnSupportPageTrue()
        : productRepository.findByFeaturedOnSupportPageTrueAndNameContainingIgnoreCase(search);

    model.addAttribute("products", products);
    model.addAttribute("search", search);
    return "support-page";
}

//dynamic product filtering 
@GetMapping("/support-page/search")
public String searchProducts(@RequestParam(required = false, defaultValue = "") String search,
                              Model model) {

    List<Product> products = search.isBlank()
        ? productRepository.findByFeaturedOnSupportPageTrue()
        : productRepository.findByFeaturedOnSupportPageTrueAndNameContainingIgnoreCase(search);

    model.addAttribute("products", products);
    return "fragments/product-cards :: cards";
}

//search assist
@GetMapping("/support-page/autocomplete")
public String autocomplete(@RequestParam(required = false, defaultValue = "") String search,
                           Model model) {
    if (search.isBlank()) return "fragments/autocomplete :: empty";

    List<Product> suggestions = productRepository
        .findByFeaturedOnSupportPageTrueAndNameStartingWithIgnoreCase(search)
        .stream()
        .limit(6)
        .collect(java.util.stream.Collectors.toList());

    model.addAttribute("suggestions", suggestions);
    return "fragments/autocomplete :: suggestions";
}


}