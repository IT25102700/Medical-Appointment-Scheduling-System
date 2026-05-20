package com.project.repository;

import com.project.model.Message;
import com.project.util.FileStorageManager;
import org.springframework.stereotype.Repository;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;

@Repository
public class MessageRepository extends BaseFileRepository<Message> {
    public MessageRepository(FileStorageManager fileStorageManager) {
        super("messages.txt", fileStorageManager);
    }

    @Override
    protected Message mapFromLine(String line) {
        String[] p = line.split(",", -1);
        String decodedContent = "";
        try {
            decodedContent = new String(Base64.getDecoder().decode(p[3]), StandardCharsets.UTF_8);
        } catch (Exception e) {
            decodedContent = p[3]; // Fallback if plain text
        }
        return new Message(
            p[0],
            p[1],
            p[2],
            decodedContent,
            LocalDateTime.parse(p[4]),
            Boolean.parseBoolean(p[5])
        );
    }

    @Override
    protected String mapToLine(Message m) {
        String encodedContent = Base64.getEncoder().encodeToString(m.getContent().getBytes(StandardCharsets.UTF_8));
        return String.join(",",
            m.getMessageId(),
            m.getSenderId(),
            m.getReceiverId(),
            encodedContent,
            m.getTimestamp().toString(),
            String.valueOf(m.isRead())
        );
    }
}
