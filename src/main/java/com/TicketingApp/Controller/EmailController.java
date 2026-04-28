package com.TicketingApp.Controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.TicketingApp.Entity.EmailMessage;
import com.TicketingApp.Entity.Ticket;
import com.TicketingApp.Entity.TicketStatus;
import com.TicketingApp.Repository.EmailMessageRepository;
import com.TicketingApp.Service.EmailService;

import com.TicketingApp.Service.TicketService;
import com.TicketingApp.Service.UserManagementService;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Controller

public class EmailController {
    private final EmailService emailService;
    private final TicketService ticketService;
    private final UserManagementService userManagementService;

    @Autowired
    private EmailMessageRepository emailMessageRepository;

    public EmailController(EmailService emailService, TicketService ticketService, UserManagementService userManagementService) {
        this.emailService = emailService;
        this.ticketService = ticketService;
        this.userManagementService = userManagementService;
    }

    @GetMapping("/")
    public String root() {
        return "redirect:/navigation-page";
    }

    // Show navigation page
    @GetMapping("/navigation-page")
    public String showNavigation() {
        return "navigation-page";
    }

    // Show staff dashboard
    @GetMapping("/staff-dashboard")
    public String showStaffDashboard(Model model, Authentication authentication) {
        model.addAttribute("tickets", ticketService.getAllTickets());
        model.addAttribute("ticketStatuses", TicketStatus.values());
        model.addAttribute("staffList", userManagementService.getAllUsers().stream()
            .filter(u -> u.getRole() != null && (u.getRole().name().equals("ROLE_STAFF") || u.getRole().name().equals("ROLE_ADMIN")))
            .toList());
        boolean isAdmin = authentication != null &&
                authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("loggedInEmail", authentication != null ? authentication.getName() : "");
        return "staff-dashboard";
    }

    // Show the form
    @GetMapping("/email-form")
    public String showForm(Model model) {
        // We provide a blank Ticket object for the form to bind to
        model.addAttribute("ticket", new Ticket());
        // Pass max file size to the template
        String maxFileSize = System.getenv("MAX_FILE_SIZE");
        if (maxFileSize == null || maxFileSize.isBlank()) {
            maxFileSize = "50MB"; // fallback default
        }
        model.addAttribute("maxFileSize", maxFileSize);
        return "email-form";
    }

    // Handle form submission
    @PostMapping("/send-email")
    public String sendEmail(@Valid @ModelAttribute("ticket") Ticket ticket, 
                            BindingResult result, 
                            Model model) {
        // Always pass maxFileSize to the template
        String maxFileSize = System.getenv("MAX_FILE_SIZE");
        if (maxFileSize == null || maxFileSize.isBlank()) {
            maxFileSize = "50MB";
        }
        model.addAttribute("maxFileSize", maxFileSize);

        // 1. Check for validation errors (Empty name, bad email, etc.)
        if (result.hasErrors()) {
            return "email-form"; 
        }

        // 2. Always save to the Database first
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
        if (ticket == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket not found");
        }
        List<EmailMessage> thread = emailMessageRepository.findByTicketOrderByTimestampAsc(ticket);
        model.addAttribute("ticket", ticket);
        model.addAttribute("ticketStatuses", TicketStatus.values());
        model.addAttribute("staffList", userManagementService.getAllUsers().stream()
            .filter(u -> u.getRole() != null && (u.getRole().name().equals("ROLE_STAFF") || u.getRole().name().equals("ROLE_ADMIN")))
            .toList());
        model.addAttribute("thread", thread);
        return "ticket-details";
    }
    @PreAuthorize("hasAnyAuthority('ROLE_STAFF', 'ROLE_ADMIN')")
    @PostMapping("/tickets/{id}/assign")
    public String updateAssignedStaff(
            @PathVariable Long id,
            @RequestParam("assignedStaff") String staffEmail,
            @RequestParam(value = "redirectTo", required = false) String redirectTo) {
        Ticket ticket = ticketService.updateAssignedStaff(id, staffEmail);
        if (ticket == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket not found");
        }
        return "redirect:" + resolveRedirectPath(id, redirectTo);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_STAFF', 'ROLE_ADMIN')")
    @PostMapping("/tickets/{id}/status")
    public String updateTicketStatus(
            @PathVariable Long id,
            @RequestParam("status") TicketStatus status,
            @RequestParam(value = "redirectTo", required = false) String redirectTo) {
        Ticket ticket = ticketService.updateStatus(id, status);
        if (ticket == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket not found");
        }

        return "redirect:" + resolveRedirectPath(id, redirectTo);
    }

        // Download endpoint removed: attachments are now served via Supabase public URL

    @PostMapping("/tickets/{id}/delete")
    public String deleteTicket(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            boolean deleted = ticketService.deleteTicket(id);
            if (deleted) {
                redirectAttributes.addFlashAttribute("deleteSuccessMessage", "Ticket deleted successfully.");
            } else {
                redirectAttributes.addFlashAttribute("deleteErrorMessage",
                        "Ticket could not be deleted because it no longer exists.");
            }
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("deleteErrorMessage",
                    "Ticket could not be deleted. Please try again.");
        }
        return "redirect:/staff-dashboard";
    }

    private static final List<String> ALLOWED_REDIRECT_PREFIXES = List.of(
        "/tickets/", "/staff-dashboard", "/admin"
    );

    private String resolveRedirectPath(Long id, String redirectTo) {
        if (redirectTo != null) {
            for (String prefix : ALLOWED_REDIRECT_PREFIXES) {
                if (redirectTo.startsWith(prefix)) {
                    return redirectTo;
                }
            }
        }
        return "/tickets/" + id;
    }
}