package com.project.service;

import com.project.model.Feedback;
import com.project.repository.FeedbackRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FeedbackService {
    private final FeedbackRepository repository;
    private final NotificationService notificationService;

    public FeedbackService(FeedbackRepository repository, NotificationService notificationService) {
        this.repository = repository;
        this.notificationService = notificationService;
    }

    public void submit(String patientId, String doctorId, int rating, String experience, String comment) {
        Feedback f = new Feedback(UUID.randomUUID().toString(), patientId, doctorId, rating, experience, comment, LocalDateTime.now());
        repository.append(f);
        notificationService.send(doctorId, "New feedback received! Rating: " + rating + " stars.");
    }

    public List<Feedback> all() {
        return repository.findAll();
    }

    public List<Feedback> findByDoctor(String doctorId) {
        return repository.findAll().stream().filter(f -> f.getDoctorId().equals(doctorId)).collect(Collectors.toList());
    }

    public double getAverageRating(String doctorId) {
        List<Feedback> feed = findByDoctor(doctorId);
        if (feed.isEmpty()) return 5.0;
        return feed.stream().mapToInt(Feedback::getRating).average().orElse(5.0);
    }

    public void delete(String feedbackId) {
        List<Feedback> all = repository.findAll();
        all.removeIf(f -> f.getFeedbackId().equals(feedbackId));
        repository.saveAll(all);
    }
}
