package com.medapp.medicalappointmentbookingapp.repository;

import com.project.util.FileStorageManager;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseFileRepository<T> {
    private final String fileName;
    private final FileStorageManager fileStorageManager;
    private List<T> cache = new ArrayList<>();
    private long cachedAt = 0L;

    protected BaseFileRepository(String fileName, FileStorageManager fileStorageManager) {
        this.fileName = fileName;
        this.fileStorageManager = fileStorageManager;
    }

    protected abstract T mapFromLine(String line);

    protected abstract String mapToLine(T item);

    public synchronized List<T> findAll() {
        if (!cache.isEmpty() && !fileStorageManager.isStale(fileName, cachedAt)) {
            return new ArrayList<>(cache);
        }
        File file = fileStorageManager.resolve(fileName);
        List<T> items = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.isBlank()) {
                    items.add(mapFromLine(line));
                }
            }
        } catch (IOException ex) {
            throw new IllegalStateException("Cannot read " + fileName, ex);
        }
        cache = items;
        cachedAt = System.currentTimeMillis();
        fileStorageManager.markRead(fileName);
        return new ArrayList<>(cache);
    }

    public synchronized void saveAll(List<T> items) {
        File file = fileStorageManager.resolve(fileName);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, false))) {
            for (T item : items) {
                writer.write(mapToLine(item));
                writer.newLine();
            }
        } catch (IOException ex) {
            throw new IllegalStateException("Cannot write " + fileName, ex);
        }
        cache = new ArrayList<>(items);
        cachedAt = System.currentTimeMillis();
        fileStorageManager.markRead(fileName);
    }

    public synchronized void append(T item) {
        File file = fileStorageManager.resolve(fileName);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write(mapToLine(item));
            writer.newLine();
        } catch (IOException ex) {
            throw new IllegalStateException("Cannot append " + fileName, ex);
        }
        cache.add(item);
        cachedAt = System.currentTimeMillis();
        fileStorageManager.markRead(fileName);
    }
}
