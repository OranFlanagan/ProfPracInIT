package com;

import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.beans.factory.annotation.Autowired;



import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

@Controller
public class EmailController{

    @Autowired
    private EmailService emailService;

    private final TicketService ticketService;

    public EmailController(TicketService ticketService) {
    this.ticketService = ticketService;
    }



    // Show the form
    @GetMapping("/email-form")
    public String showForm(Model model) {
        model.addAttribute("ticket", new Ticket());
        return "email-form"; // maps to email-form.html
    }

    // Handle form submission
    @PostMapping("/send-email")
    public String sendEmail(@Valid @ModelAttribute("ticket") Ticket ticket,BindingResult result,Model model)
    {       

            //this should always go through without depending on email service 
            
            ticketService.createTicket(ticket);

        try {
            emailService.sendSimpleEmail(ticket);
            String attachmentInfo = (ticket.getAttachment() != null && !ticket.getAttachment().isEmpty()) 
                ? " with attachment" 
                : "";
            model.addAttribute("successMessage", "✅ Ticket submitted successfully to " + ticket.getEmail() + attachmentInfo);
            model.addAttribute("ticket", new Ticket()); // reset form
        } catch (Exception e) {
            model.addAttribute("errorMessage", "❌ Failed to send ticket: " + e.getMessage());
        }



             try {
                //Commented out for time being due to auth issue
                //emailService.sendSimpleEmail(ticket);
                model.addAttribute("successMessage", " Ticket submitted successfully to " + ticket.getEmail());
                model.addAttribute("ticket", new Ticket()); // reset form
                } 
            catch (Exception e) 
            {
             model.addAttribute("errorMessage", " Failed to send ticket: " + e.getMessage());
            }

            return "email-form";
    }
}
