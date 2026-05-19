package com.medapp.medicalappointmentbookingapp.controller;

import com.project.model.AppointmentStatus;
import com.project.service.*;
import com.project.util.FileStorageManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

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
    public String dashboard(Model model) {
        model.addAttribute("doctors", doctorService.findAllDoctors());
        model.addAttribute("appointments", appointmentService.all());
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

    @GetMapping("/patient/book")
    public String bookView(@RequestParam String doctorId, Model model) {
        model.addAttribute("doctor", doctorService.findAllDoctors().stream()
                .filter(d -> d.getDoctorId().equals(doctorId))
                .findFirst()
                .orElse(null));
        return "member6-appointment-booking/booking";
    }

    @PostMapping("/patient/book")
    public String book(Authentication authentication, @RequestParam String doctorId, @RequestParam String date, @RequestParam String time, @RequestParam(defaultValue = "") String notes, Model model) {
        LocalDateTime ldt = LocalDateTime.now();
        try {
            int hour = Integer.parseInt(time.split(":")[0]);
            int min = Integer.parseInt(time.split(":")[1].split(" ")[0]);
            if (time.contains("PM") && hour < 12) hour += 12;
            if (time.contains("AM") && hour == 12) hour = 0;
            ldt = LocalDateTime.of(java.time.LocalDate.parse(date), java.time.LocalTime.of(hour, min));
        } catch (Exception e) {}

        var user = userService.getUserRepository().findByUsername(authentication.getName()).orElseThrow();
        var appointment = appointmentService.book(user.getUserId(), doctorId, ldt, notes);
        
        double fee = 500.0;
        var doc = doctorService.findAllDoctors().stream().filter(d -> d.getDoctorId().equals(doctorId)).findFirst();
        if (doc.isPresent()) fee = doc.get().getAppointmentFee();
        
        model.addAttribute("appointmentId", appointment.getAppointmentId());
        model.addAttribute("fee", fee);
        return "member6-appointment-booking/checkout";
    }

    @PostMapping("/patient/pay")
    public String processPayment(@RequestParam String appointmentId, @RequestParam double amount, @RequestParam String method) {
        paymentService.submit(appointmentId, amount, method);
        return "redirect:/patient/dashboard";
    }

    @PostMapping("/patient/cancel")
    public String cancel(@RequestParam String appointmentId) {
        appointmentService.updateStatus(appointmentId, AppointmentStatus.CANCELLED);
        paymentService.refundByAppointment(appointmentId);
        return "redirect:/patient/dashboard";
    }

    @GetMapping("/patient/reschedule")
    public String rescheduleView(@RequestParam String appointmentId, Model model) {
        var appt = appointmentService.findById(appointmentId);
        model.addAttribute("appointment", appt);
        model.addAttribute("doctor", doctorService.findAllDoctors().stream().filter(d -> d.getDoctorId().equals(appt.getDoctorId())).findFirst().orElse(null));
        return "member6-appointment-booking/reschedule";
    }

    @PostMapping("/patient/reschedule")
    public String reschedule(@RequestParam String appointmentId, @RequestParam String date, @RequestParam String time) {
        LocalDateTime ldt = LocalDateTime.now();
        try {
            int hour = Integer.parseInt(time.split(":")[0]);
            int min = Integer.parseInt(time.split(":")[1].split(" ")[0]);
            if (time.contains("PM") && hour < 12) hour += 12;
            if (time.contains("AM") && hour == 12) hour = 0;
            ldt = LocalDateTime.of(java.time.LocalDate.parse(date), java.time.LocalTime.of(hour, min));
        } catch (Exception e) {}
        appointmentService.reschedule(appointmentId, ldt);
        return "redirect:/patient/dashboard";
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

    @GetMapping("/patient/feedback")
    public String feedbackView(@RequestParam String appointmentId, @RequestParam String doctorId, Model model) {
        model.addAttribute("appointmentId", appointmentId);
        model.addAttribute("doctor", doctorService.findAllDoctors().stream().filter(d -> d.getDoctorId().equals(doctorId)).findFirst().orElse(null));
        return "member5-feedback/feedback-form";
    }

    @PostMapping("/patient/feedback/submit")
    public String submitFeedback(Authentication authentication, @RequestParam String doctorId, @RequestParam int rating, @RequestParam String experience, @RequestParam String comment) {
        String username = authentication.getName();
        var patient = userService.getUserRepository().findByUsername(username).orElseThrow();
        feedbackService.submit(patient.getUserId(), doctorId, rating, experience, comment);
        return "redirect:/patient/dashboard";
    }
}
