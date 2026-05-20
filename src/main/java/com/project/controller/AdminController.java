package com.project.controller;

import com.project.model.Doctor;
import com.project.model.Role;
import com.project.repository.UserRepository;
import com.project.service.*;
import com.project.util.FileStorageManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AdminController {
    private final AdminService adminService;
    private final UserService userService;
    private final DoctorService doctorService;
    private final AppointmentService appointmentService;
    private final PaymentService paymentService;
    private final com.project.service.FeedbackService feedbackService;
    private final com.project.service.NotificationService notificationService;
    private final FileStorageManager fileStorageManager;

    public AdminController(AdminService adminService, UserService userService, DoctorService doctorService, AppointmentService appointmentService, PaymentService paymentService, com.project.service.FeedbackService feedbackService, com.project.service.NotificationService notificationService, FileStorageManager fileStorageManager) {
        this.adminService = adminService;
        this.userService = userService;
        this.doctorService = doctorService;
        this.appointmentService = appointmentService;
        this.paymentService = paymentService;
        this.feedbackService = feedbackService;
        this.notificationService = notificationService;
        this.fileStorageManager = fileStorageManager;
    }

    private void addDashboardStats(Model model) {
        model.addAttribute("stats", adminService.dashboardStats());
        model.addAttribute("users", userService.getUserRepository().findAll());
        model.addAttribute("doctors", doctorService.findAllDoctors());
        model.addAttribute("appointments", appointmentService.all());
        model.addAttribute("payments", paymentService.history());
        model.addAttribute("feedbacks", feedbackService.all());
    }

    @GetMapping("/admin/dashboard")
    public String dashboard(Model model) {
        addDashboardStats(model);
        return "member3-admin-dashboard/dashboard";
    }

    @GetMapping("/admin/doctors")
    public String doctorsList(Model model) {
        model.addAttribute("doctors", doctorService.findAllDoctors());
        return "member2-doctor-dashboard/doctors";
    }

    @GetMapping("/admin/appointments")
    public String appointmentsList(Model model) {
        addDashboardStats(model);
        return "member3-admin-dashboard/appointments";
    }

    @GetMapping("/admin/payments")
    public String paymentsList(Model model) {
        addDashboardStats(model);
        return "member3-admin-dashboard/payments";
    }

    @PostMapping("/admin/appointments/cancel")
    public String cancelAppointment(@RequestParam String appointmentId) {
        appointmentService.updateStatus(appointmentId, com.project.model.AppointmentStatus.CANCELLED);
        return "redirect:/admin/appointments";
    }

    @GetMapping("/admin/feedbacks")
    public String feedbacksList(Model model) {
        addDashboardStats(model);
        return "member5-feedback/feedback-list";
    }

    @PostMapping("/admin/feedbacks/delete")
    public String deleteFeedback(@RequestParam String feedbackId) {
        feedbackService.delete(feedbackId);
        return "redirect:/admin/feedbacks";
    }

    @GetMapping("/admin/doctors/add")
    public String addDoctorView() {
        return "member3-admin-dashboard/add-doctor";
    }

    @PostMapping("/admin/doctors/add")
    public String addDoctor(@RequestParam String username,
                            @RequestParam String password,
                            @RequestParam String fullName,
                            @RequestParam String email,
                            @RequestParam String phone,
                            @RequestParam String specialization,
                            @RequestParam String licenseNumber,
                            @RequestParam(defaultValue = "0") double appointmentFee,
                            @RequestParam(defaultValue = "") String experience,
                            @RequestParam(defaultValue = "") String degree,
                            @RequestParam(defaultValue = "") String about,
                            @RequestParam(defaultValue = "") String availableSlots,
                            @RequestParam(required = false) MultipartFile profileImageFile,
                            RedirectAttributes redirectAttributes) {
        try {
            UserRepository.SimpleUser account = userService.register(username, password, Role.ROLE_DOCTOR, fullName, email);
            Doctor doctor = new Doctor(
                    account.getUserId(),
                    username,
                    "",
                    fullName,
                    email,
                    phone,
                    specialization,
                    licenseNumber
            );
            doctor.setAppointmentFee(appointmentFee);
            doctor.setExperience(experience);
            doctor.setDegree(degree);
            doctor.setAbout(about);
            doctor.setAvailableSlots(availableSlots);
            
            if (profileImageFile != null && !profileImageFile.isEmpty()) {
                String imagePath = fileStorageManager.saveImage(profileImageFile);
                doctor.setProfileImage(imagePath);
            }
            doctorService.saveDoctor(doctor);
            
            // Notify all admins
            userService.getUserRepository().findAll().stream()
                .filter(u -> u.getRole() == com.project.model.Role.ROLE_ADMIN)
                .forEach(admin -> notificationService.send(admin.getUserId(), "New doctor registered: " + fullName));
                
            redirectAttributes.addFlashAttribute("message", "Doctor added successfully.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/admin/doctors/add";
    }

    @GetMapping("/admin/profile")
    public String profile(Authentication authentication, Model model) {
        String username = authentication.getName();
        model.addAttribute("admin", userService.getUserRepository().findByUsername(username).orElse(null));
        return "member4-patient-dashboard/profile";
    }

    @PostMapping("/admin/profile/update")
    public String updateProfile(Authentication authentication,
                                @RequestParam String fullName,
                                @RequestParam String email,
                                @RequestParam String phone,
                                @RequestParam(required = false) MultipartFile profileImageFile) {
        String username = authentication.getName();
        var admin = userService.getUserRepository().findByUsername(username).orElseThrow();
        
        admin.setFullName(fullName);
        admin.setEmail(email);
        admin.setPhone(phone);
        
        if (profileImageFile != null && !profileImageFile.isEmpty()) {
            String imagePath = fileStorageManager.saveImage(profileImageFile);
            admin.setProfileImage(imagePath);
        }
        
        userService.getUserRepository().upsert(admin);
        return "redirect:/admin/profile";
    }
}
