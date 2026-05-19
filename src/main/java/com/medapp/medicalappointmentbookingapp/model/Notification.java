package com.medapp.medicalappointmentbookingapp.model;

import java.time.LocalDateTime;

public class Notification {
    private String notificationId;
    private String userId;
    private String message;
    private LocalDateTime timestamp;
    private boolean isRead;

    public Notification() {}

    public Notification(String notificationId, String userId, String message, LocalDateTime timestamp) {
        this.notificationId = notificationId;
        this.userId = userId;
        this.message = message;
        this.timestamp = timestamp;
        this.isRead = false;
    }

    public String getNotificationId() { return notificationId; }
    public void setNotificationId(String notificationId) { this.notificationId = notificationId; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }
}
