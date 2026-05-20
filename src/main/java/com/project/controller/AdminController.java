package com.project.controller;

import com.project.model.Doctor;
import com.project.model.Role;
import com.project.repository.UserRepository;
import com.project.service.AdminService;
import com.project.service.AppointmentService;
import com.project.service.DoctorService;
import com.project.service.PaymentService;
import com.project.service.UserService;
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
        return "member3-admin-dashboard/doctors";
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

    @GetMapping("/admin/doctors/edit")
    public String editDoctorView(@RequestParam String doctorId, Model model) {
        var doctor = doctorService.findAllDoctors().stream()
                .filter(d -> d.getDoctorId().equals(doctorId) || d.getUserId().equals(doctorId))
                .findFirst()
                .orElse(null);
        model.addAttribute("doctor", doctor);
        return "member3-admin-dashboard/edit-doctor";
    }

    @PostMapping("/admin/doctors/edit")
    public String editDoctor(@RequestParam String doctorId,
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
                             @RequestParam(required = false) String password,
                             @RequestParam(required = false) MultipartFile profileImageFile,
                             RedirectAttributes redirectAttributes) {
        try {
            var doctor = doctorService.findAllDoctors().stream()
                    .filter(d -> d.getDoctorId().equals(doctorId) || d.getUserId().equals(doctorId))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Doctor not found"));

            // Sync user account details
            var user = userService.getUserRepository().findByUserId(doctor.getUserId()).orElseThrow();
            user.setFullName(fullName);
            user.setEmail(email);
            user.setPhone(phone);
            if (password != null && !password.isBlank()) {
                user.setPasswordHash(userService.getPasswordEncoder().encode(password));
            }
            userService.getUserRepository().upsert(user);

            // Update doctor details
            doctor.setFullName(fullName);
            doctor.setEmail(email);
            doctor.setPhone(phone);
            doctor.setSpecialization(specialization);
            doctor.setLicenseNumber(licenseNumber);
            doctor.setAppointmentFee(appointmentFee);
            doctor.setExperience(experience);
            doctor.setDegree(degree);
            doctor.setAbout(about);
            doctor.setAvailableSlots(availableSlots);

            if (profileImageFile != null && !profileImageFile.isEmpty()) {
                String imagePath = fileStorageManager.saveImage(profileImageFile);
                doctor.setProfileImage(imagePath);
                user.setProfileImage(imagePath);
                userService.getUserRepository().upsert(user);
            }

            doctorService.saveDoctor(doctor);
            redirectAttributes.addFlashAttribute("message", "Doctor profile updated successfully.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/admin/doctors";
    }

    @PostMapping("/admin/doctors/delete")
    public String deleteDoctor(@RequestParam String doctorId, RedirectAttributes redirectAttributes) {
        try {
            var doctor = doctorService.findAllDoctors().stream()
                    .filter(d -> d.getDoctorId().equals(doctorId) || d.getUserId().equals(doctorId))
                    .findFirst()
                    .orElse(null);
            if (doctor != null) {
                // Delete user from User Repository
                userService.getUserRepository().delete(doctor.getUserId());
                // Delete doctor from Doctor Repository
                userService.getDoctorRepository().delete(doctor.getDoctorId());
                redirectAttributes.addFlashAttribute("message", "Doctor deleted successfully.");
            } else {
                redirectAttributes.addFlashAttribute("error", "Doctor not found.");
            }
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/admin/doctors";
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
