package com.project.controller;

import com.project.service.DoctorService;
import com.project.service.FeedbackService;
import com.project.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class FeedbackController {
    private final FeedbackService feedbackService;
    private final DoctorService doctorService;
    private final UserService userService;

    public FeedbackController(FeedbackService feedbackService, DoctorService doctorService, UserService userService) {
        this.feedbackService = feedbackService;
        this.doctorService = doctorService;
        this.userService = userService;
    }

    @GetMapping("/patient/feedback")
    public String feedbackView(@RequestParam String appointmentId, @RequestParam String doctorId, Model model) {
        model.addAttribute("appointmentId", appointmentId);
        model.addAttribute("doctor", doctorService.findAllDoctors().stream()
                .filter(d -> d.getDoctorId().equals(doctorId))
                .findFirst()
                .orElse(null));
        return "member5-feedback/feedback-form";
    }

    @PostMapping("/patient/feedback/submit")
    public String submitFeedback(Authentication authentication,
                                 @RequestParam String doctorId,
                                 @RequestParam int rating,
                                 @RequestParam String experience,
                                 @RequestParam String comment) {
        String username = authentication.getName();
        var patient = userService.getUserRepository().findByUsername(username).orElseThrow();
        feedbackService.submit(patient.getUserId(), doctorId, rating, experience, comment);
        return "redirect:/patient/appointments";
    }

    @GetMapping("/patient/feedbacks")
    public String patientFeedbacks(Authentication authentication, Model model) {
        String username = authentication.getName();
        var patient = userService.getUserRepository().findByUsername(username).orElseThrow();
        var myFeedbacks = feedbackService.all().stream()
                .filter(f -> f.getPatientId().equals(patient.getUserId()))
                .collect(java.util.stream.Collectors.toList());
        model.addAttribute("feedbacks", myFeedbacks);
        model.addAttribute("doctors", doctorService.findAllDoctors());
        return "member5-feedback/patient-feedbacks";
    }

    @PostMapping("/patient/feedbacks/delete")
    public String deletePatientFeedback(Authentication authentication, @RequestParam String feedbackId) {
        String username = authentication.getName();
        var patient = userService.getUserRepository().findByUsername(username).orElseThrow();
        var feedback = feedbackService.all().stream()
                .filter(f -> f.getFeedbackId().equals(feedbackId))
                .findFirst()
                .orElse(null);
        if (feedback != null && feedback.getPatientId().equals(patient.getUserId())) {
            feedbackService.delete(feedbackId);
        }
        return "redirect:/patient/feedbacks";
    }
}
