package com.TicketingApp.Controller;

import com.TicketingApp.Entity.LoginForm;
import com.TicketingApp.Service.SupabaseAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LoginController {

    @Autowired
    private SupabaseAdminService supabaseAdminService;

    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("loginForm", new LoginForm());
        return "login";
    }

    @PostMapping("/login/forgot-password")
    public String forgotPassword(@RequestParam String email, RedirectAttributes redirectAttributes) {
        supabaseAdminService.sendPasswordReset(email);
        redirectAttributes.addFlashAttribute("successMessage",
            "If that email is registered, a password reset link has been sent.");
        return "redirect:/login";
    }
}

   