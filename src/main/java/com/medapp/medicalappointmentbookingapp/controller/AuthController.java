package com.medapp.medicalappointmentbookingapp.controller;

import com.project.model.Role;
import com.project.service.UserService;
import jakarta.validation.constraints.NotBlank;
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
            userService.register(username, password, Role.valueOf(role), fullName, email);
            model.addAttribute("message", "Registered successfully. Please log in.");
            return "member1-home-login/login";
        } catch (Exception ex) {
            model.addAttribute("error", ex.getMessage());
            return "member1-home-login/register";
        }
    }

}
