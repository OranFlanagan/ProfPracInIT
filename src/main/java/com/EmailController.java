package com;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class EmailController {

    private final EmailService emailService;
    private final TicketService ticketService;

    @Autowired
    private EmailMessageRepository emailMessageRepository;

    public EmailController(EmailService emailService, TicketService ticketService) {
        this.emailService = emailService;
        this.ticketService = ticketService;
    }

    // Show navigation page
    @GetMapping("/navigation-page")
    public String showNavigation() {
        return "navigation-page";
    }

    // Show staff dashboard
    @GetMapping("/staff-dashboard")
    public String showStaffDashboard(Model model) {
        model.addAttribute("tickets", ticketService.getAllTickets());
        return "staff-dashboard";
    }

    // Show the form
    @GetMapping("/email-form")
    public String showForm(Model model) {
        // We provide a blank Ticket object for the form to bind to
        model.addAttribute("ticket", new Ticket());
        return "email-form"; 
    }

    // Handle form submission
    @PostMapping("/send-email")
    public String sendEmail(@Valid @ModelAttribute("ticket") Ticket ticket, 
                            BindingResult result, 
                            Model model) {

        // 1. Check for validation errors (Empty name, bad email, etc.)
        if (result.hasErrors()) {
            return "email-form"; 
        }

        // 2. Always save to the H2 Database first
        // This ensures the data is kept even if the email fails
        try {
            ticketService.createTicket(ticket);
        } catch (Exception e) {
            model.addAttribute("errorMessage", "❌ Database Error: Could not save ticket.");
            return "email-form";
        }

        // 3. Attempt to send the email
        try {
            emailService.sendSimpleEmail(ticket);
            
            String attachmentInfo = (ticket.getAttachment() != null && !ticket.getAttachment().isEmpty()) 
                ? " with attachment" 
                : "";
                
            model.addAttribute("successMessage", "✅ Ticket #" + ticket.getOrderNum() + 
                               " saved and sent to " + ticket.getEmail() + attachmentInfo);
            
            // Reset the form after success
            model.addAttribute("ticket", new Ticket()); 
            
        } catch (Exception e) {
            // If email fails, we tell the user the ticket was still saved to the DB
            model.addAttribute("errorMessage", "⚠️ Ticket saved to database, but email failed: " + e.getMessage());
        }

        return "email-form";
    }

    // Ticket detail view
    @GetMapping("/tickets/{id}")
    public String viewTicket(@PathVariable Long id, Model model) {
        Ticket ticket = ticketService.findById(id);
        List<EmailMessage> thread = emailMessageRepository.findByTicketOrderByTimestampAsc(ticket);
        model.addAttribute("ticket", ticket);
        model.addAttribute("thread", thread);
        return "ticket-details";
    }

    // Download attachment for a ticket
    @GetMapping("/tickets/{id}/attachment")
    public ResponseEntity<byte[]> downloadAttachment(@PathVariable Long id) {
        Ticket ticket = ticketService.findById(id);
        if (ticket == null || ticket.getAttachmentData() == null) {
            return ResponseEntity.notFound().build();
        }
        String contentType = ticket.getAttachmentContentType() != null
                ? ticket.getAttachmentContentType()
                : "application/octet-stream";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + ticket.getAttachmentFilename() + "\"")
                .contentType(MediaType.parseMediaType(contentType))
                .body(ticket.getAttachmentData());
    }
}