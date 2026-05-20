package com.project.service;

import com.project.model.Doctor;
import com.project.model.DoctorSchedule;
import com.project.repository.DoctorRepository;
import com.project.repository.DoctorScheduleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DoctorService {
    private final DoctorRepository doctorRepository;
    private final DoctorScheduleRepository scheduleRepository;

    public DoctorService(DoctorRepository doctorRepository, DoctorScheduleRepository scheduleRepository) {
        this.doctorRepository = doctorRepository;
        this.scheduleRepository = scheduleRepository;
    }

    public List<Doctor> findAllDoctors() {
        return doctorRepository.findAll();
    }

    public List<Doctor> findBySpecialization(String specialization) {
        return doctorRepository.findAll().stream()
                .filter(d -> d.getSpecialization().equalsIgnoreCase(specialization))
                .collect(Collectors.toList());
    }

    public void saveDoctor(Doctor doctor) {
        doctorRepository.upsert(doctor);
    }

    public void saveSchedule(DoctorSchedule schedule) {
        if (schedule.getScheduleId() == null || schedule.getScheduleId().isBlank()) {
            schedule.setScheduleId("S-" + UUID.randomUUID());
        }
        scheduleRepository.upsert(schedule);
    }

    public List<DoctorSchedule> findSchedules() {
        return scheduleRepository.findAll();
    }
}
