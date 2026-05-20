package com.project.repository;

import com.project.model.Feedback;
import com.project.util.FileStorageManager;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public class FeedbackRepository extends BaseFileRepository<Feedback> {
    public FeedbackRepository(FileStorageManager manager) { super("feedback.txt", manager); }
    @Override protected Feedback mapFromLine(String line) {
        String[] p = line.split(",", -1);
        return new Feedback(p[0], p[1], p[2], Integer.parseInt(p[3]), p[4], p[5], LocalDateTime.parse(p[6]));
    }
    @Override protected String mapToLine(Feedback f) {
        return String.join(",", f.getFeedbackId(), f.getPatientId(), f.getDoctorId(), String.valueOf(f.getRating()), f.getExperience(), f.getComment(), f.getSubmittedAt().toString());
    }
}
