package com.project.controller;

import com.project.model.DoctorSchedule;
import com.project.service.AppointmentService;
import com.project.service.DoctorService;
import com.project.service.PatientRecordService;
import com.project.service.UserService;
import com.project.util.FileStorageManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class DoctorController {
    private final DoctorService doctorService;
    private final AppointmentService appointmentService;
    private final PatientRecordService recordService;
    private final UserService userService;
    private final com.project.service.PaymentService paymentService;
    private final FileStorageManager fileStorageManager;

    public DoctorController(DoctorService doctorService, AppointmentService appointmentService, PatientRecordService recordService, UserService userService, com.project.service.PaymentService paymentService, FileStorageManager fileStorageManager) {
        this.doctorService = doctorService;
        this.appointmentService = appointmentService;
        this.recordService = recordService;
        this.userService = userService;
        this.paymentService = paymentService;
        this.fileStorageManager = fileStorageManager;
    }

    @GetMapping("/doctor/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        String username = authentication.getName();
        var doctor = doctorService.findAllDoctors().stream().filter(d -> d.getUsername().equals(username)).findFirst().orElseThrow();
        
        var doctorAppts = appointmentService.doctorAppointments(doctor.getDoctorId());
        var completedApptIds = doctorAppts.stream()
                .filter(a -> a.getStatus() == com.project.model.AppointmentStatus.COMPLETED)
                .map(com.project.model.Appointment::getAppointmentId)
                .collect(java.util.stream.Collectors.toList());
        
        double totalEarnings = paymentService.history().stream()
                .filter(p -> completedApptIds.contains(p.getAppointmentId()))
                .mapToDouble(com.project.model.Payment::getAmount)
                .sum();
        
        model.addAttribute("appointments", doctorAppts);
        model.addAttribute("patients", userService.getPatientRepository().findAll());
        model.addAttribute("schedules", doctorService.findSchedules());
        model.addAttribute("records", recordService.all());
        model.addAttribute("earnings", totalEarnings);
        return "member2-doctor-dashboard/dashboard";
    }

    @GetMapping("/doctor/appointments")
    public String appointments(Model model) {
        model.addAttribute("appointments", appointmentService.all());
        model.addAttribute("patients", userService.getPatientRepository().findAll());
        return "member2-doctor-dashboard/appointments";
    }


    @GetMapping("/doctor/profile")
    public String profile(Authentication authentication, Model model) {
        String username = authentication.getName();
        model.addAttribute("doctor", doctorService.findAllDoctors().stream()
                .filter(d -> d.getUsername().equals(username))
                .findFirst()
                .orElse(null));
        return "member4-patient-dashboard/profile";
    }

    @PostMapping("/doctor/profile/update")
    public String updateProfile(Authentication authentication,
                                @RequestParam String fullName,
                                @RequestParam String email,
                                @RequestParam String phone,
                                @RequestParam(required = false) MultipartFile profileImageFile,
                                @RequestParam String specialization,
                                @RequestParam String licenseNumber,
                                @RequestParam(defaultValue = "0") double appointmentFee,
                                @RequestParam(defaultValue = "") String experience,
                                @RequestParam(defaultValue = "") String degree,
                                @RequestParam(defaultValue = "") String about,
                                @RequestParam(defaultValue = "") String availableSlots) {
        String username = authentication.getName();
        var doctor = doctorService.findAllDoctors().stream()
                .filter(d -> d.getUsername().equals(username))
                .findFirst()
                .orElseThrow();
        
        doctor.setFullName(fullName);
        doctor.setEmail(email);
        doctor.setPhone(phone);
        
        if (profileImageFile != null && !profileImageFile.isEmpty()) {
            String imagePath = fileStorageManager.saveImage(profileImageFile);
            doctor.setProfileImage(imagePath);
        }
        
        doctor.setSpecialization(specialization);
        doctor.setLicenseNumber(licenseNumber);
        doctor.setAppointmentFee(appointmentFee);
        doctor.setExperience(experience);
        doctor.setDegree(degree);
        doctor.setAbout(about);
        doctor.setAvailableSlots(availableSlots);
        
        doctorService.saveDoctor(doctor);
        return "redirect:/doctor/profile";
    }

    @PostMapping("/doctor/appointments/complete")
    public String completeAppointment(@RequestParam String appointmentId) {
        appointmentService.updateStatus(appointmentId, com.project.model.AppointmentStatus.COMPLETED);
        return "redirect:/doctor/appointments";
    }

    @PostMapping("/doctor/appointments/cancel")
    public String cancelAppointment(@RequestParam String appointmentId) {
        appointmentService.updateStatus(appointmentId, com.project.model.AppointmentStatus.CANCELLED);
        return "redirect:/doctor/appointments";
    }

    @PostMapping("/doctor/schedule")
    public String addSchedule(@RequestParam String doctorId, @RequestParam String dayOfWeek, @RequestParam String startTime, @RequestParam String endTime) {
        DoctorSchedule schedule = new DoctorSchedule();
        schedule.setDoctorId(doctorId);
        schedule.setDayOfWeek(dayOfWeek);
        schedule.setStartTime(startTime);
        schedule.setEndTime(endTime);
        doctorService.saveSchedule(schedule);
        return "redirect:/doctor/dashboard";
    }
}
