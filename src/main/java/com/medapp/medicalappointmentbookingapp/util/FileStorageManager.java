package com.medapp.medicalappointmentbookingapp.util;

import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class FileStorageManager {
    private static final String BASE_PATH = "src/main/resources/data";
    private final Map<String, Long> lastReadMap = new ConcurrentHashMap<>();

    public synchronized File resolve(String fileName) {
        try {
            Path path = Path.of(BASE_PATH, fileName);
            Files.createDirectories(path.getParent());
            if (!Files.exists(path)) {
                Files.createFile(path);
            }
            return path.toFile();
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to resolve file: " + fileName, ex);
        }
    }

    public synchronized boolean isStale(String fileName, long cachedAt) {
        File file = resolve(fileName);
        long modified = file.lastModified();
        Long lastRead = lastReadMap.getOrDefault(fileName, 0L);
        return modified > cachedAt || modified > lastRead;
    }

    public synchronized void markRead(String fileName) {
        lastReadMap.put(fileName, System.currentTimeMillis());
    }

    public String saveImage(org.springframework.web.multipart.MultipartFile file) {
        if (file == null || file.isEmpty()) return null;
        try {
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path uploadPath = Path.of("src/main/resources/static/uploads");
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath);
            return "/uploads/" + fileName;
        } catch (IOException ex) {
            throw new IllegalStateException("Could not save image", ex);
        }
    }
}

