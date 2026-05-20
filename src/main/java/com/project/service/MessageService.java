package com.project.service;

import com.project.model.Message;
import com.project.repository.MessageRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MessageService {
    private final MessageRepository repository;

    public MessageService(MessageRepository repository) {
        this.repository = repository;
    }

    public void sendMessage(String senderId, String receiverId, String content) {
        if (content == null || content.trim().isEmpty()) return;
        Message m = new Message(UUID.randomUUID().toString(), senderId, receiverId, content.trim(), LocalDateTime.now(), false);
        repository.append(m);
    }

    public List<Message> getConversation(String user1, String user2) {
        return repository.findAll().stream()
                .filter(m -> (m.getSenderId().equals(user1) && m.getReceiverId().equals(user2))
                        || (m.getSenderId().equals(user2) && m.getReceiverId().equals(user1)))
                .sorted((a, b) -> a.getTimestamp().compareTo(b.getTimestamp()))
                .collect(Collectors.toList());
    }

    public List<Message> getMessagesForUser(String userId) {
        return repository.findAll().stream()
                .filter(m -> m.getSenderId().equals(userId) || m.getReceiverId().equals(userId))
                .sorted((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()))
                .collect(Collectors.toList());
    }

    public void markAsRead(String senderId, String receiverId) {
        List<Message> all = repository.findAll();
        boolean changed = false;
        for (Message m : all) {
            if (m.getSenderId().equals(senderId) && m.getReceiverId().equals(receiverId) && !m.isRead()) {
                m.setRead(true);
                changed = true;
            }
        }
        if (changed) {
            repository.saveAll(all);
        }
    }
}
