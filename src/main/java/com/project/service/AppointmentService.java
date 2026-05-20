package com.project.service;

import com.project.model.Appointment;
import com.project.model.AppointmentStatus;
import com.project.repository.AppointmentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AppointmentService {
    private final AppointmentRepository repository;
    private final NotificationService notificationService;

    public AppointmentService(AppointmentRepository repository, NotificationService notificationService) {
        this.repository = repository;
        this.notificationService = notificationService;
    }

    public Appointment book(String patientId, String doctorId, LocalDateTime time, String notes) {
        boolean occupied = repository.findAll().stream()
                .anyMatch(a -> a.getDoctorId().equals(doctorId)
                        && a.getAppointmentTime().equals(time)
                        && a.getStatus() != AppointmentStatus.CANCELLED);
        if (occupied) {
            throw new IllegalArgumentException("Doctor is unavailable for selected time");
        }
        Appointment appointment = new Appointment();
        appointment.setAppointmentId("A-" + UUID.randomUUID());
        appointment.setPatientId(patientId);
        appointment.setDoctorId(doctorId);
        appointment.setAppointmentTime(time);
        appointment.setStatus(AppointmentStatus.BOOKED);
        appointment.setNotes(notes);
        repository.append(appointment);
        
        notificationService.send(doctorId, "New appointment request from patient: " + patientId);
        notificationService.send(patientId, "Appointment booked successfully for " + time.toString());
        
        return appointment;
    }

    public void updateStatus(String appointmentId, AppointmentStatus status) {
        Appointment appointment = findById(appointmentId);
        appointment.setStatus(status);
        repository.upsert(appointment);
        
        notificationService.send(appointment.getPatientId(), "Your appointment status has been updated to: " + status);
        if (status == AppointmentStatus.CANCELLED) {
            notificationService.send(appointment.getDoctorId(), "Appointment " + appointmentId + " has been cancelled.");
        }
    }

    public Appointment findById(String id) {
        return repository.findAll().stream().filter(a -> a.getAppointmentId().equals(id)).findFirst().orElseThrow();
    }

    public List<Appointment> patientAppointments(String patientId) {
        return repository.findAll().stream().filter(a -> a.getPatientId().equals(patientId)).collect(Collectors.toList());
    }

    public List<Appointment> doctorAppointments(String doctorId) {
        return repository.findAll().stream().filter(a -> a.getDoctorId().equals(doctorId)).collect(Collectors.toList());
    }

    public void reschedule(String appointmentId, LocalDateTime newTime) {
        Appointment appointment = findById(appointmentId);
        LocalDateTime oldTime = appointment.getAppointmentTime();
        appointment.setAppointmentTime(newTime);
        repository.upsert(appointment);
        
        notificationService.send(appointment.getDoctorId(), "Appointment " + appointmentId + " has been rescheduled from " + oldTime + " to " + newTime);
        notificationService.send(appointment.getPatientId(), "Your appointment has been rescheduled to " + newTime);
    }

    public List<Appointment> all() {
        return repository.findAll();
    }
}
