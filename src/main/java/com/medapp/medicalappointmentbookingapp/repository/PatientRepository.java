package com.medapp.medicalappointmentbookingapp.repository;

import com.project.model.Patient;
import com.project.util.FileStorageManager;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PatientRepository extends BaseFileRepository<Patient> {
    public PatientRepository(FileStorageManager manager) { super("patients.txt", manager); }
    @Override protected Patient mapFromLine(String line) {
        String[] p = line.split(",", -1);
        Patient patient = new Patient(p[0], p[1], "", p[2], p[3], p[4], p[5], p[6]);
        if (p.length > 7) patient.setProfileImage(p[7]);
        return patient;
    }
    @Override protected String mapToLine(Patient patient) {
        return String.join(",", patient.getUserId(), patient.getUsername(), patient.getFullName(), patient.getEmail(), patient.getPhone(), patient.getBloodGroup(), patient.getAllergies(), patient.getProfileImage() == null ? "" : patient.getProfileImage());
    }

    public void upsert(Patient patient) {
        List<Patient> patients = findAll();
        patients.removeIf(d -> d.getUserId().equals(patient.getUserId()));
        patients.add(patient);
        saveAll(patients);
    }
}
