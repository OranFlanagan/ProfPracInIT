package com.TicketingApp.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.ui.Model;

import com.TicketingApp.Repository.ProductRepository;
import com.TicketingApp.Entity.Product;
import java.util.*;

@Controller
public class supportPageController {


@Autowired
private ProductRepository productRepository;


@GetMapping("/support-page")
public String getSupportPage(Model model) {


    List<Product> products = productRepository.findByFeaturedOnSupportPageTrue();

    model.addAttribute("products", products);

    return "support-page";
}


}