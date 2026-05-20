package com.project.controller;

import com.project.model.Feedback;
import com.project.service.FeedbackService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/feedback")
public class FeedbackController {
    private final FeedbackService feedbackService;

    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @GetMapping
    public String listFeedbacks(Model model) {
        List<Feedback> feedbacks = feedbackService.all();
        model.addAttribute("feedbacks", feedbacks);
        return "feedback/list"; // JSP view
    }

    @PostMapping("/submit")
    public String submitFeedback(@RequestParam String patientId, 
                                 @RequestParam String doctorId, 
                                 @RequestParam int rating, 
                                 @RequestParam String experience, 
                                 @RequestParam String comment) {
        feedbackService.submit(patientId, doctorId, rating, experience, comment);
        return "redirect:/feedback";
    }

    @PostMapping("/delete")
    public String deleteFeedback(@RequestParam String feedbackId) {
        feedbackService.delete(feedbackId);
        return "redirect:/feedback";
    }
}
