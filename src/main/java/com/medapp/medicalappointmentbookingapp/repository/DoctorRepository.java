package com.medapp.medicalappointmentbookingapp.repository;

import com.project.model.Doctor;
import com.project.util.FileStorageManager;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DoctorRepository extends BaseFileRepository<Doctor> {
    public DoctorRepository(FileStorageManager manager) { super("doctors.txt", manager); }
    
    @Override protected Doctor mapFromLine(String line) {
        String[] p = line.split(",", -1);
        Doctor d = new Doctor(p[0], p[1], "", p[2], p[3], p[4], p[5], p[6]);
        if (p.length > 7) d.setProfileImage(p[7]);
        if (p.length > 8) d.setAppointmentFee(p[8].isEmpty() ? 0 : Double.parseDouble(p[8]));
        if (p.length > 9) d.setExperience(p[9]);
        if (p.length > 10) d.setDegree(p[10]);
        if (p.length > 11) d.setAbout(p[11].replace("___", ","));
        if (p.length > 12) d.setAvailableSlots(p[12]);
        return d;
    }

    @Override protected String mapToLine(Doctor d) {
        return String.join(",", 
            d.getUserId(), 
            d.getUsername(), 
            d.getFullName(), 
            d.getEmail(), 
            d.getPhone(), 
            d.getSpecialization(), 
            d.getLicenseNumber(), 
            d.getProfileImage() == null ? "" : d.getProfileImage(),
            String.valueOf(d.getAppointmentFee()),
            d.getExperience() == null ? "" : d.getExperience(),
            d.getDegree() == null ? "" : d.getDegree(),
            d.getAbout() == null ? "" : d.getAbout().replace(",", "___"),
            d.getAvailableSlots() == null ? "" : d.getAvailableSlots()
        );
    }

    public void upsert(Doctor doctor) {
        List<Doctor> doctors = findAll();
        doctors.removeIf(d -> d.getUserId().equals(doctor.getUserId()));
        doctors.add(doctor);
        saveAll(doctors);
    }

    public void delete(String doctorId) {
        List<Doctor> doctors = findAll();
        doctors.removeIf(d -> d.getDoctorId().equals(doctorId) || d.getUserId().equals(doctorId));
        saveAll(doctors);
    }
}
