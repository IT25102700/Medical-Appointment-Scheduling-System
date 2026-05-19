package com.medapp.medicalappointmentbookingapp.model;

import java.time.LocalDateTime;

public class Feedback {
    private String feedbackId;
    private String patientId;
    private String doctorId;
    private int rating;
    private String experience;
    private String comment;
    private LocalDateTime submittedAt;

    public Feedback() {}

    public Feedback(String feedbackId, String patientId, String doctorId, int rating, String experience, String comment, LocalDateTime submittedAt) {
        this.feedbackId = feedbackId;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.rating = rating;
        this.experience = experience;
        this.comment = comment;
        this.submittedAt = submittedAt;
    }

    public String getFeedbackId() { return feedbackId; }
    public void setFeedbackId(String feedbackId) { this.feedbackId = feedbackId; }
    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }
    public String getDoctorId() { return doctorId; }
    public void setDoctorId(String doctorId) { this.doctorId = doctorId; }
    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }
    public String getExperience() { return experience; }
    public void setExperience(String experience) { this.experience = experience; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }
}
