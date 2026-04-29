package com.TicketingApp.Controller;

import com.TicketingApp.Entity.UserVariable;
import com.TicketingApp.Service.UserManagementService;
import com.TicketingApp.Service.SupabaseAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.TicketingApp.Service.EmailTemplateService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminController {

    @Autowired
    private UserManagementService userManagementService;

    @Autowired
    private EmailTemplateService emailTemplateService;

    @Autowired
    private SupabaseAdminService supabaseAdminService;

    @GetMapping
    public String showAdminDashboard(Model model, Authentication authentication) {
        model.addAttribute("users", userManagementService.getAllUsers());
        model.addAttribute("roles", UserVariable.Role.values());
        model.addAttribute("ticketEmailTemplate", emailTemplateService.getTicketSubmittedTemplate());
        model.addAttribute("loggedInEmail", authentication != null ? authentication.getName() : "");
        return "admin-dashboard";
    }

    @PostMapping("/staff")
    public String registerStaff(@RequestParam String email,
                                @RequestParam UserVariable.Role role,
                                RedirectAttributes redirectAttributes) {
        if (email == null || email.isBlank()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Email is required.");
            return "redirect:/admin";
        }
        String inviteError = supabaseAdminService.inviteUser(email);
        if (inviteError == null) {
            try {
                userManagementService.registerUser(email, role);
                redirectAttributes.addFlashAttribute("successMessage",
                    "User '" + email + "' invited successfully. They must check their email to set a password.");
            } catch (Exception e) {
                supabaseAdminService.deleteUser(email);
                redirectAttributes.addFlashAttribute("errorMessage",
                    "Failed to save user record. Please try again.");
            }
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", inviteError);
        }
        return "redirect:/admin";
    }

    @PostMapping("/email-template")
    public String updateTicketEmailTemplate(@RequestParam String templateContent, RedirectAttributes redirectAttributes) {
        if (templateContent == null || templateContent.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Email template cannot be empty.");
            return "redirect:/admin";
        }
        emailTemplateService.updateTicketSubmittedTemplate(templateContent.trim());
        redirectAttributes.addFlashAttribute("successMessage", "Ticket submission email template updated.");
        return "redirect:/admin";
    }

    @PostMapping("/staff/{email}/reset-password")
    public String resetPassword(@PathVariable String email, RedirectAttributes redirectAttributes) {
        boolean success = supabaseAdminService.sendPasswordReset(email);
        if (success) {
            redirectAttributes.addFlashAttribute("successMessage", "Password reset email sent to '" + email + "'.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to send password reset email. User may not exist.");
        }
        return "redirect:/admin";
    }

    @PostMapping("/staff/{id}/role")
    public String updateUserRole(@PathVariable Long id,
                                 @RequestParam UserVariable.Role role,
                                 RedirectAttributes redirectAttributes) {
        boolean updated = userManagementService.updateUserRole(id, role);
        if (updated) {
            redirectAttributes.addFlashAttribute("successMessage", "User role updated successfully.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "User not found.");
        }
        return "redirect:/admin";
    }

    @PostMapping("/staff/{id}/delete")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            UserVariable user = userManagementService.findById(id);
            if (user == null) {
                redirectAttributes.addFlashAttribute("deleteErrorMessage",
                        "User could not be deleted because it no longer exists.");
                return "redirect:/admin";
            }
            boolean supabaseDeleted = supabaseAdminService.deleteUser(user.getEmail());
            if (!supabaseDeleted) {
                redirectAttributes.addFlashAttribute("deleteErrorMessage",
                        "Failed to delete user from authentication system. Please try again.");
                return "redirect:/admin";
            }
            userManagementService.deleteUser(id);
            redirectAttributes.addFlashAttribute("deleteSuccessMessage", "User deleted successfully.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("deleteErrorMessage",
                    "User could not be deleted. Please try again.");
        }
        return "redirect:/admin";
    }

    @PostMapping("/staff/{id}/toggle-notifications")
    public String toggleNotifications(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        boolean toggled = userManagementService.toggleNotifications(id);
        if (toggled) {
            redirectAttributes.addFlashAttribute("successMessage", "Notification preference updated.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "User not found.");
        }
        return "redirect:/admin";
    }
}
