package com.TicketingApp.Controller;

import com.TicketingApp.Entity.Users.Roles;
import com.TicketingApp.Service.UserManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserManagementService userManagementService;

    @GetMapping
    public String showAdminDashboard(Model model) {
        model.addAttribute("users", userManagementService.getAllUsers());
        model.addAttribute("roles", Roles.values());
        return "admin-dashboard";
    }

    @PostMapping("/staff")
    public String registerStaff(@RequestParam String username,
                                @RequestParam String password,
                                @RequestParam Roles role,
                                RedirectAttributes redirectAttributes) {
        if (username == null || username.isBlank()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Username is required.");
            return "redirect:/admin";
        }
        if (password == null || password.isBlank()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Password is required.");
            return "redirect:/admin";
        }
        if (userManagementService.usernameExists(username)) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Username '" + username + "' is already taken.");
            return "redirect:/admin";
        }

        userManagementService.registerUser(username, password, role);
        redirectAttributes.addFlashAttribute("successMessage",
                "User '" + username + "' registered successfully.");
        return "redirect:/admin";
    }

    @PostMapping("/staff/{id}/reset-password")
    public String resetPassword(@PathVariable Long id,
                                @RequestParam String newPassword,
                                RedirectAttributes redirectAttributes) {
        if (newPassword == null || newPassword.isBlank()) {
            redirectAttributes.addFlashAttribute("errorMessage", "New password is required.");
            return "redirect:/admin";
        }
        boolean success = userManagementService.resetPassword(id, newPassword);
        if (success) {
            redirectAttributes.addFlashAttribute("successMessage", "Password reset successfully.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "User not found.");
        }
        return "redirect:/admin";
    }

    @PostMapping("/staff/{id}/delete")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        boolean success = userManagementService.deleteUser(id);
        if (success) {
            redirectAttributes.addFlashAttribute("successMessage", "User deleted successfully.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "User not found.");
        }
        return "redirect:/admin";
    }
}
