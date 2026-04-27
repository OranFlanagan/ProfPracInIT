package com.TicketingApp.Controller;

import com.TicketingApp.Service.SupabaseAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/invite")
public class InviteController {

    @Autowired
    private SupabaseAuthService supabaseAuthService;

    @GetMapping("/accept")
    public String acceptInvitePage() {
        return "invite-accept";
    }

    @PostMapping("/accept")
    public String acceptInvite(@RequestParam String accessToken,
                               @RequestParam String password,
                               RedirectAttributes redirectAttributes) {
        boolean success = supabaseAuthService.setPassword(accessToken, password);
        if (success) {
            redirectAttributes.addFlashAttribute("successMessage", "Password set successfully. You can now log in.");
            return "redirect:/login";
        }
        redirectAttributes.addFlashAttribute("errorMessage", "Failed to set password. Your invite link may have expired.");
        return "redirect:/invite/accept";
    }
}
