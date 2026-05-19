package com.medapp.medicalappointmentbookingapp.controller;

import com.project.service.NotificationService;
import com.project.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class NotificationController {
    private final NotificationService notificationService;
    private final UserService userService;

    public NotificationController(NotificationService notificationService, UserService userService) {
        this.notificationService = notificationService;
        this.userService = userService;
    }

    @GetMapping("/notifications")
    public String viewNotifications(Authentication authentication, Model model) {
        String username = authentication.getName();
        var user = userService.getUserRepository().findByUsername(username).orElseThrow();
        model.addAttribute("notifications", notificationService.findByUser(user.getUserId()));
        return "fragments/notifications-list"; // Or a dedicated page
    }

    @PostMapping("/notifications/read")
    public String markAsRead(@RequestParam String notificationId) {
        notificationService.markAsRead(notificationId);
        return "redirect:/notifications";
    }
}
