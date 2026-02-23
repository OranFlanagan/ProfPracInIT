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
        model.addAttribute("emailRequest", new EmailRequest());
        return "email-form"; // maps to email-form.html
    }

    // Handle form submission
    @PostMapping("/send-email")
    public String sendEmail(@Valid @ModelAttribute("emailRequest") EmailRequest emailRequest,
                            BindingResult result,
                            Model model) {

        if (result.hasErrors()) {
            return "email-form"; // return to form with validation errors
        }

        try {
            emailService.sendSimpleEmail(
                emailRequest.getTo(),
                emailRequest.getSubject(),
                emailRequest.getBody()
            );
            model.addAttribute("successMessage", "✅ Email sent successfully to " + emailRequest.getTo());
            model.addAttribute("emailRequest", new EmailRequest()); // reset form
        } catch (Exception e) {
            model.addAttribute("errorMessage", "❌ Failed to send email: " + e.getMessage());
        }

        return "email-form";
    }
}
