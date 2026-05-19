package com.medapp.medicalappointmentbookingapp.repository;

import com.project.model.Appointment;
import com.project.model.AppointmentStatus;
import com.project.util.FileStorageManager;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class AppointmentRepository extends BaseFileRepository<Appointment> {
    public AppointmentRepository(FileStorageManager manager) { super("appointments.txt", manager); }
    @Override protected Appointment mapFromLine(String line) {
        String[] p = line.split(",", -1);
        Appointment a = new Appointment();
        a.setAppointmentId(p[0]); a.setPatientId(p[1]); a.setDoctorId(p[2]);
        a.setAppointmentTime(LocalDateTime.parse(p[3])); a.setStatus(AppointmentStatus.valueOf(p[4])); a.setNotes(p[5]);
        return a;
    }
    @Override protected String mapToLine(Appointment a) {
        return String.join(",", a.getAppointmentId(), a.getPatientId(), a.getDoctorId(), a.getAppointmentTime().toString(), a.getStatus().name(), a.getNotes() == null ? "" : a.getNotes());
    }
    public void upsert(Appointment appointment) {
        List<Appointment> list = findAll();
        list.removeIf(a -> a.getAppointmentId().equals(appointment.getAppointmentId()));
        list.add(appointment);
        saveAll(list);
    }
}
