package com.medapp.medicalappointmentbookingapp.model;

public class Doctor extends User {
    private String specialization;
    private String licenseNumber;
    private double appointmentFee;
    private String experience;
    private String degree;
    private String about;
    private String availableSlots; // Comma separated slots like "09:00 AM,10:00 AM"

    public Doctor() {
        setRole(Role.ROLE_DOCTOR);
    }

    public Doctor(String userId, String username, String passwordHash, String fullName, String email, String phone, String specialization, String licenseNumber) {
        super(userId, username, passwordHash, Role.ROLE_DOCTOR, fullName, email, phone);
        this.specialization = specialization;
        this.licenseNumber = licenseNumber;
    }

    public String getDoctorId() {
        return getUserId();
    }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public String getLicenseNumber() { return licenseNumber; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }

    public double getAppointmentFee() { return appointmentFee; }
    public void setAppointmentFee(double appointmentFee) { this.appointmentFee = appointmentFee; }

    public String getExperience() { return experience; }
    public void setExperience(String experience) { this.experience = experience; }

    public String getDegree() { return degree; }
    public void setDegree(String degree) { this.degree = degree; }

    public String getAbout() { return about; }
    public void setAbout(String about) { this.about = about; }

    public String getAvailableSlots() { return availableSlots; }
    public void setAvailableSlots(String availableSlots) { this.availableSlots = availableSlots; }
}
