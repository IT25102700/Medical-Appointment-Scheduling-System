package com.medapp.medicalappointmentbookingapp.controller;

import com.project.service.DoctorService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    private final DoctorService doctorService;

    public HomeController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @GetMapping("/")
    public String root(Model model) {
        model.addAttribute("doctors", doctorService.findAllDoctors());
        return "member1-home-login/index";
    }

    @GetMapping("/about")
    public String about() {
        return "member1-home-login/about";
    }

    @GetMapping("/contact")
    public String contact() {
        return "member1-home-login/contact";
    }


    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication) {
        if (authentication == null) return "redirect:/login";
        if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return "redirect:/admin/dashboard";
        }
        if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_DOCTOR"))) {
            return "redirect:/doctor/dashboard";
        }
        return "redirect:/patient/dashboard";
    }
}
