package com.medapp.medicalappointmentbookingapp.service;

import com.project.model.Notification;
import com.project.repository.NotificationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class NotificationService {
    private final NotificationRepository repository;

    public NotificationService(NotificationRepository repository) {
        this.repository = repository;
    }

    public void send(String userId, String message) {
        Notification n = new Notification(UUID.randomUUID().toString(), userId, message, LocalDateTime.now());
        repository.append(n);
    }

    public List<Notification> findByUser(String userId) {
        return repository.findAll().stream()
                .filter(n -> n.getUserId().equals(userId))
                .sorted((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()))
                .collect(Collectors.toList());
    }

    public void markAsRead(String notificationId) {
        List<Notification> all = repository.findAll();
        for (Notification n : all) {
            if (n.getNotificationId().equals(notificationId)) {
                n.setRead(true);
                break;
            }
        }
        repository.saveAll(all);
    }

    public long unreadCount(String userId) {
        return repository.findAll().stream()
                .filter(n -> n.getUserId().equals(userId) && !n.isRead())
                .count();
    }
}
