package com.projects.service;

import com.project.model.Role;
import com.project.repository.AdminRepository;
import com.project.repository.DoctorRepository;
import com.project.repository.PatientRepository;
import com.project.repository.UserRepository;
import jakarta.validation.constraints.NotBlank;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PatientRepository patientRepository, DoctorRepository doctorRepository, AdminRepository adminRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserRepository.SimpleUser register(@NotBlank String username, @NotBlank String password, Role role, String fullName, String email) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }
        UserRepository.SimpleUser user = new UserRepository.SimpleUser("U-" + UUID.randomUUID(), username, passwordEncoder.encode(password), role);
        user.setFullName(fullName);
        user.setEmail(email);
        userRepository.append(user);
        return user;
    }

    public void resetPassword(String userId, String rawPassword) {
        UserRepository.SimpleUser user = userRepository.findByUserId(userId).orElseThrow();
        user.setPasswordHash(passwordEncoder.encode(rawPassword));
        userRepository.upsert(user);
    }

    public PasswordEncoder getPasswordEncoder() {
        return passwordEncoder;
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }

    public PatientRepository getPatientRepository() {
        return patientRepository;
    }

    public DoctorRepository getDoctorRepository() {
        return doctorRepository;
    }

    public AdminRepository getAdminRepository() {
        return adminRepository;
    }
}
