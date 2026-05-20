package com.project.repository;

import com.project.model.Notification;
import com.project.util.FileStorageManager;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public class NotificationRepository extends BaseFileRepository<Notification> {
    public NotificationRepository(FileStorageManager manager) {
        super("notifications.txt", manager);
    }

    @Override
    protected Notification mapFromLine(String line) {
        String[] p = line.split(",", -1);
        Notification n = new Notification(p[0], p[1], p[2], LocalDateTime.parse(p[3]));
        n.setRead(Boolean.parseBoolean(p[4]));
        return n;
    }

    @Override
    protected String mapToLine(Notification n) {
        return String.join(",", 
            n.getNotificationId(), 
            n.getUserId(), 
            n.getMessage(), 
            n.getTimestamp().toString(), 
            String.valueOf(n.isRead())
        );
    }
}
