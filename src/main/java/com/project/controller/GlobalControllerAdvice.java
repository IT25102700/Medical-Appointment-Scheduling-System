package com.project.controller;

import com.project.service.NotificationService;
import com.project.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {
    private final NotificationService notificationService;
    private final UserService userService;

    public GlobalControllerAdvice(NotificationService notificationService, UserService userService) {
        this.notificationService = notificationService;
        this.userService = userService;
    }

    @ModelAttribute("unreadNotificationsCount")
    public long unreadNotificationsCount(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return 0;
        }
        String username = authentication.getName();
        return userService.getUserRepository().findByUsername(username)
                .map(user -> notificationService.unreadCount(user.getUserId()))
                .orElse(0L);
    }
}
