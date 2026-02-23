package com;

import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.beans.factory.annotation.Autowired;

@Controller
public class EmailController{

    @Autowired
    private EmailService emailService;

    // Show the form
    @GetMapping("/email-form")
    public String showForm(Model model) {
        model.addAttribute("ticket", new Ticket());
        return "email-form"; // maps to email-form.html
    }

    // Handle form submission
    @PostMapping("/send-email")
    public String sendEmail(@Valid @ModelAttribute("ticket") Ticket ticket,
                            BindingResult result,
                            Model model) {

        if (result.hasErrors()) {
            return "email-form"; // return to form with validation errors
        }

        try {
            emailService.sendSimpleEmail(ticket);
            model.addAttribute("successMessage", "✅ Ticket submitted successfully to " + ticket.getEmail());
            model.addAttribute("ticket", new Ticket()); // reset form
        } catch (Exception e) {
            model.addAttribute("errorMessage", "❌ Failed to send ticket: " + e.getMessage());
        }

        return "email-form";
    }
}
