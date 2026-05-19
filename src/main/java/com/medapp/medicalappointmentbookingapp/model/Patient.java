package com.medapp.medicalappointmentbookingapp.model;

public class Patient extends User {
    private String bloodGroup;
    private String allergies;

    public Patient() {
        setRole(Role.ROLE_PATIENT);
    }

    public Patient(String userId, String username, String passwordHash, String fullName, String email, String phone, String bloodGroup, String allergies) {
        super(userId, username, passwordHash, Role.ROLE_PATIENT, fullName, email, phone);
        this.bloodGroup = bloodGroup;
        this.allergies = allergies;
    }

    public String getPatientId() {
        return getUserId();
    }


    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public String getAllergies() {
        return allergies;
    }

    public void setAllergies(String allergies) {
        this.allergies = allergies;
    }
}
