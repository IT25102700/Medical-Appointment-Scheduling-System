package com.medapp.medicalappointmentbookingapp.config;

import com.project.model.Doctor;
import com.project.model.Patient;
import com.project.model.Role;
import com.project.repository.UserRepository;
import com.project.service.DoctorService;
import com.project.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {
    private final UserService userService;
    private final DoctorService doctorService;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserService userService, DoctorService doctorService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.doctorService = doctorService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userService.getUserRepository().findAll().isEmpty()) {
            userService.getUserRepository().append(new UserRepository.SimpleUser("U-ADMIN", "admin", passwordEncoder.encode("admin123"), Role.ROLE_ADMIN));
            userService.getUserRepository().append(new UserRepository.SimpleUser("U-DR", "doctor1", passwordEncoder.encode("doctor123"), Role.ROLE_DOCTOR));
            userService.getUserRepository().append(new UserRepository.SimpleUser("U-PT", "patient1", passwordEncoder.encode("patient123"), Role.ROLE_PATIENT));
        }

        if (doctorService.findAllDoctors().isEmpty()) {
            Doctor doctor = new Doctor("U-DR", "doctor1", "", "Dr. Brown", "doctor@hospital.local", "9000000001", "Cardiology", "LIC-2026-11");
            doctorService.saveDoctor(doctor);
        }

        if (userService.getPatientRepository().findAll().isEmpty()) {
            Patient patient = new Patient("U-PT", "patient1", "", "Alice Smith", "alice@mail.local", "9000000002", "O+", "None");
            userService.getPatientRepository().upsert(patient);
        }
    }
}
