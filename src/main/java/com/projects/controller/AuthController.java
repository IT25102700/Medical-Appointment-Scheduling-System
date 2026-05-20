package com.projects.controller;

import com.project.model.Role;
import com.project.service.UserService;
import jakarta.validation.constraints.NotBlank;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {
    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String login(@RequestParam(required = false) String error,
                        @RequestParam(required = false) String logout,
                        Model model) {
        if (error != null) {
            model.addAttribute("error", "Invalid username or password.");
        }
        if (logout != null) {
            model.addAttribute("message", "Logged out successfully.");
        }
        return "member1-home-login/login";
    }

    @GetMapping("/register")
    public String registerView() {
        return "member1-home-login/register";
    }

    @PostMapping("/register")
    public String register(@RequestParam @NotBlank String username,
                           @RequestParam @NotBlank String password,
                           @RequestParam String fullName,
                           @RequestParam String email,
                           @RequestParam String role,
                           Model model) {
        try {
            // Publicly only ROLE_PATIENT registration is permitted
            userService.register(username, password, Role.ROLE_PATIENT, fullName, email);
            model.addAttribute("message", "Registered successfully. Please log in.");
            return "member1-home-login/login";
        } catch (Exception ex) {
            model.addAttribute("error", ex.getMessage());
            return "member1-home-login/register";
        }
    }

    @GetMapping("/change-password")
    public String changePasswordView(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        return "member1-home-login/change-password";
    }

    @PostMapping("/change-password")
    public String changePassword(Authentication authentication,
                                 jakarta.servlet.http.HttpServletRequest request,
                                 @RequestParam String currentPassword,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmPassword,
                                 org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes,
                                 Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        String username = authentication.getName();
        try {
            var user = userService.getUserRepository().findByUsername(username).orElseThrow();
            if (!userService.getPasswordEncoder().matches(currentPassword, user.getPasswordHash())) {
                model.addAttribute("error", "Current password does not match.");
                return "member1-home-login/change-password";
            }
            if (!newPassword.equals(confirmPassword)) {
                model.addAttribute("error", "New passwords do not match.");
                return "member1-home-login/change-password";
            }
            if (newPassword.isBlank()) {
                model.addAttribute("error", "Password cannot be empty.");
                return "member1-home-login/change-password";
            }
            userService.resetPassword(user.getUserId(), newPassword);
            try {
                request.logout();
            } catch (Exception e) {}
            redirectAttributes.addFlashAttribute("message", "Password changed successfully. Please log in with your new password.");
            return "redirect:/login";
        } catch (Exception ex) {
            model.addAttribute("error", ex.getMessage());
        }
        return "member1-home-login/change-password";
    }
}
