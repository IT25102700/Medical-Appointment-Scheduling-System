package com.project.controller;

import com.project.service.DoctorService;
import com.project.service.FeedbackService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class HomeController {
    private final DoctorService doctorService;
    private final FeedbackService feedbackService;

    public HomeController(DoctorService doctorService, FeedbackService feedbackService) {
        this.doctorService = doctorService;
        this.feedbackService = feedbackService;
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
        if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_FRONTDESK"))) {
            return "redirect:/frontdesk/dashboard";
        }
        return "redirect:/patient/dashboard";
    }

    @PostMapping("/general-review")
    public String submitGeneralReview(@RequestParam String name,
                                      @RequestParam int rating,
                                      @RequestParam String comment,
                                      RedirectAttributes redirectAttributes) {
        feedbackService.submitGeneralReview(name, rating, comment);
        redirectAttributes.addFlashAttribute("reviewMessage", "Thank you for your feedback! Your review has been sent to the administration.");
        return "redirect:/";
    }
}
