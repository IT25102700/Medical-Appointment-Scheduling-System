package com.medapp.medicalappointmentbookingapp.controller;

import com.project.service.*;
import com.project.util.FileStorageManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class PatientController {
    private final DoctorService doctorService;
    private final AppointmentService appointmentService;
    private final PaymentService paymentService;
    private final PatientRecordService recordService;
    private final UserService userService;
    private final com.project.service.FeedbackService feedbackService;
    private final FileStorageManager fileStorageManager;

    public PatientController(DoctorService doctorService, AppointmentService appointmentService, PaymentService paymentService, PatientRecordService recordService, UserService userService, com.project.service.FeedbackService feedbackService, FileStorageManager fileStorageManager) {
        this.doctorService = doctorService;
        this.appointmentService = appointmentService;
        this.paymentService = paymentService;
        this.recordService = recordService;
        this.userService = userService;
        this.feedbackService = feedbackService;
        this.fileStorageManager = fileStorageManager;
    }

    @GetMapping("/patient/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        String username = authentication.getName();
        var patient = userService.getUserRepository().findByUsername(username).orElseThrow();
        var appointments = appointmentService.patientAppointments(patient.getUserId());
        var apptIds = appointments.stream().map(com.project.model.Appointment::getAppointmentId).collect(java.util.stream.Collectors.toList());
        
        var payments = paymentService.history().stream()
                .filter(p -> apptIds.contains(p.getAppointmentId()))
                .collect(java.util.stream.Collectors.toList());
        
        long completedCount = appointments.stream().filter(a -> a.getStatus() == com.project.model.AppointmentStatus.COMPLETED).count();
        long bookedCount = appointments.stream().filter(a -> a.getStatus() == com.project.model.AppointmentStatus.BOOKED).count();
        double totalPaid = payments.stream().mapToDouble(com.project.model.Payment::getAmount).sum();

        model.addAttribute("appointments", appointments);
        model.addAttribute("doctors", doctorService.findAllDoctors());
        model.addAttribute("patient", patient);
        model.addAttribute("bookedCount", bookedCount);
        model.addAttribute("completedCount", completedCount);
        model.addAttribute("totalPaid", totalPaid);
        return "member4-patient-dashboard/dashboard";
    }

    @GetMapping("/patient/appointments")
    public String appointments(Authentication authentication, Model model) {
        String username = authentication.getName();
        var patient = userService.getUserRepository().findByUsername(username).orElseThrow();
        var appointments = appointmentService.patientAppointments(patient.getUserId());
        
        model.addAttribute("doctors", doctorService.findAllDoctors());
        model.addAttribute("appointments", appointments);
        model.addAttribute("payments", paymentService.history());
        model.addAttribute("records", recordService.all());
        return "member4-patient-dashboard/my-appointments";
    }

    @GetMapping("/doctors")
    public String doctorsList(@RequestParam(required = false) String specialty, Model model) {
        var doctors = doctorService.findAllDoctors();
        if (specialty != null && !specialty.isEmpty()) {
            doctors = doctors.stream()
                    .filter(d -> d.getSpecialization().equalsIgnoreCase(specialty))
                    .collect(java.util.stream.Collectors.toList());
        }
        model.addAttribute("doctors", doctors);
        model.addAttribute("selectedSpecialty", specialty);
        
        java.util.Map<String, Double> ratings = new java.util.HashMap<>();
        for (var doc : doctors) {
            ratings.put(doc.getDoctorId(), feedbackService.getAverageRating(doc.getDoctorId()));
        }
        model.addAttribute("ratings", ratings);
        
        return "member2-doctor-dashboard/doctors";
    }

    @PostMapping("/patient/pay")
    public String processPayment(@RequestParam String appointmentId, @RequestParam double amount, @RequestParam String method) {
        paymentService.submit(appointmentId, amount, method);
        return "redirect:/patient/appointments";
    }

    @GetMapping("/patient/payments")
    public String paymentHistory(Authentication authentication, Model model) {
        String username = authentication.getName();
        var patient = userService.getUserRepository().findByUsername(username).orElseThrow();
        var appointments = appointmentService.patientAppointments(patient.getUserId());
        var apptIds = appointments.stream().map(com.project.model.Appointment::getAppointmentId).collect(java.util.stream.Collectors.toList());
        
        var payments = paymentService.history().stream()
                .filter(p -> apptIds.contains(p.getAppointmentId()))
                .collect(java.util.stream.Collectors.toList());
        
        model.addAttribute("payments", payments);
        model.addAttribute("appointments", appointments);
        model.addAttribute("doctors", doctorService.findAllDoctors());
        return "member4-patient-dashboard/payment-history";
    }

    @GetMapping("/patient/profile")
    public String profile(Authentication authentication, Model model) {
        String username = authentication.getName();
        model.addAttribute("patient", userService.getPatientRepository().findAll().stream()
                .filter(p -> p.getUsername().equals(username))
                .findFirst()
                .orElse(null));
        return "member4-patient-dashboard/profile";
    }

    @PostMapping("/patient/profile/update")
    public String updateProfile(Authentication authentication,
                                @RequestParam String fullName,
                                @RequestParam String email,
                                @RequestParam String phone,
                                @RequestParam(required = false) MultipartFile profileImageFile,
                                @RequestParam String bloodGroup,
                                @RequestParam String allergies) {
        String username = authentication.getName();
        var patient = userService.getPatientRepository().findAll().stream()
                .filter(p -> p.getUsername().equals(username))
                .findFirst()
                .orElseThrow();
        
        patient.setFullName(fullName);
        patient.setEmail(email);
        patient.setPhone(phone);
        
        if (profileImageFile != null && !profileImageFile.isEmpty()) {
            String imagePath = fileStorageManager.saveImage(profileImageFile);
            patient.setProfileImage(imagePath);
        }
        
        patient.setBloodGroup(bloodGroup);
        patient.setAllergies(allergies);
        
        userService.getPatientRepository().upsert(patient);
        return "redirect:/patient/profile";
    }


}
