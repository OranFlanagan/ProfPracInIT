package com.TicketingApp.Controller;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.TicketingApp.Entity.Ticket;
import com.TicketingApp.Entity.TicketStatus;
import com.TicketingApp.Service.EmailService;
import com.TicketingApp.Service.SupabaseStorageService;
import com.TicketingApp.Service.TicketService;
import com.TicketingApp.Service.UserManagementService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Controller
public class EmailController {
    private static final Logger logger = LoggerFactory.getLogger(EmailController.class);

    private final EmailService emailService;
    private final TicketService ticketService;
    private final UserManagementService userManagementService;
    private final SupabaseStorageService supabaseStorageService;

    public EmailController(EmailService emailService, TicketService ticketService,
                           UserManagementService userManagementService,
                           SupabaseStorageService supabaseStorageService) {
        this.emailService = emailService;
        this.ticketService = ticketService;
        this.userManagementService = userManagementService;
        this.supabaseStorageService = supabaseStorageService;
    }

   

    @GetMapping("/navigation-page")
    public String showNavigation() {
        return "navigation-page";
    }

    @GetMapping("/staff-dashboard")
    public String showStaffDashboard(Model model, Authentication authentication,
                                     @RequestParam(defaultValue = "") String sort) {
        List<Ticket> tickets;
        if ("oldest".equals(sort)) {
            tickets = ticketService.getAllTicketsSorted(true);
        } else if ("newest".equals(sort)) {
            tickets = ticketService.getAllTicketsSorted(false);
        } else {
            tickets = ticketService.getAllTickets();
        }
        model.addAttribute("tickets", tickets);
        model.addAttribute("sort", sort);
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

    @GetMapping("/email-form")
    public String showForm(Model model) {
        model.addAttribute("ticket", new Ticket());
        String maxFileSize = System.getenv("MAX_FILE_SIZE");
        if (maxFileSize == null || maxFileSize.isBlank()) {
            maxFileSize = "50MB";
        }
        model.addAttribute("maxFileSize", maxFileSize);
        return "email-form";
    }

    @PostMapping("/send-email")
    public String sendEmail(@Valid @ModelAttribute("ticket") Ticket ticket,
                            BindingResult result,
                            Model model) {
        String maxFileSize = System.getenv("MAX_FILE_SIZE");
        if (maxFileSize == null || maxFileSize.isBlank()) {
            maxFileSize = "50MB";
        }
        model.addAttribute("maxFileSize", maxFileSize);

        if (result.hasErrors()) {
            return "email-form";
        }

        try {
            ticketService.createTicket(ticket);
        } catch (Exception e) {
            logger.error("Failed to save ticket", e);
            model.addAttribute("errorMessage", "❌ Database Error: Could not save ticket. Please try again or contact support.");
            return "email-form";
        }

        try {
            emailService.sendSimpleEmail(ticket);
            String attachmentInfo = (ticket.getAttachment() != null && !ticket.getAttachment().isEmpty())
                ? " with attachment"
                : "";
            model.addAttribute("successMessage", "✅ Ticket #" + ticket.getOrderNum() +
                               " saved and sent to " + ticket.getEmail() + attachmentInfo);
            model.addAttribute("ticket", new Ticket());
        } catch (Exception e) {
            model.addAttribute("errorMessage", "⚠️ Ticket saved to database, but email could not be sent. Please contact support.");
        }

        emailService.sendNewTicketNotification(ticket, userManagementService.getNotificationEmails());

        return "email-form";
    }

    @GetMapping("/tickets/{id}")
    public String viewTicket(@PathVariable Long id, Model model) {
        Ticket ticket = ticketService.findById(id);
        if (ticket == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket not found");
        }
        if (ticket.getSupabaseFilename() != null && !ticket.getSupabaseFilename().isBlank()) {
            try {
                ticket.setAttachmentUrl(supabaseStorageService.getSignedUrl(ticket.getSupabaseFilename()));
            } catch (Exception e) {
                logger.warn("Could not generate signed URL for ticket {}: {}", id, e.getMessage());
            }
        }
        model.addAttribute("ticket", ticket);
        model.addAttribute("ticketStatuses", TicketStatus.values());
        model.addAttribute("staffList", userManagementService.getAllUsers().stream()
            .filter(u -> u.getRole() != null && (u.getRole().name().equals("ROLE_STAFF") || u.getRole().name().equals("ROLE_ADMIN")))
            .toList());
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

    @PreAuthorize("hasAnyAuthority('ROLE_STAFF', 'ROLE_ADMIN')")
    @PostMapping("/tickets/{id}/internal-notes")
    public String updateInternalNotes(
            @PathVariable Long id,
            @RequestParam(required = false, defaultValue = "") String internalNotes,
            RedirectAttributes redirectAttributes) {
        Ticket ticket = ticketService.updateInternalNotes(id, internalNotes);
        if (ticket == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket not found");
        }
        redirectAttributes.addFlashAttribute("successMessage", "Internal notes saved.");
        return "redirect:/tickets/" + id;
    }

    @PreAuthorize("hasAnyAuthority('ROLE_STAFF', 'ROLE_ADMIN')")
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
