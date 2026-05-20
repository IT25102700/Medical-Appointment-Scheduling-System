package com.project.controller;

import com.project.model.Message;
import com.project.service.DoctorService;
import com.project.service.MessageService;
import com.project.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class MessageController {
    private final MessageService messageService;
    private final UserService userService;
    private final DoctorService doctorService;

    public MessageController(MessageService messageService, UserService userService, DoctorService doctorService) {
        this.messageService = messageService;
        this.userService = userService;
        this.doctorService = doctorService;
    }

    // ==========================================
    // ADMIN MESSAGES
    // ==========================================
    @GetMapping("/admin/messages")
    public String adminMessages(Authentication authentication, 
                                @RequestParam(required = false) String doctorId, 
                                Model model) {
        String adminUsername = authentication.getName();
        var admin = userService.getUserRepository().findByUsername(adminUsername).orElseThrow();

        model.addAttribute("doctors", doctorService.findAllDoctors());
        model.addAttribute("selectedDoctorId", doctorId);

        if (doctorId != null && !doctorId.isBlank()) {
            messageService.markAsRead(doctorId, admin.getUserId());
            List<Message> chat = messageService.getConversation(admin.getUserId(), doctorId);
            model.addAttribute("conversation", chat);
            
            var selectedDoctor = doctorService.findAllDoctors().stream()
                    .filter(d -> d.getDoctorId().equals(doctorId))
                    .findFirst()
                    .orElse(null);
            model.addAttribute("selectedDoctor", selectedDoctor);
        }
        return "member3-admin-dashboard/messages";
    }

    @PostMapping("/admin/messages/send")
    public String adminSendMessage(Authentication authentication, 
                                   @RequestParam String doctorId, 
                                   @RequestParam String content) {
        String adminUsername = authentication.getName();
        var admin = userService.getUserRepository().findByUsername(adminUsername).orElseThrow();
        messageService.sendMessage(admin.getUserId(), doctorId, content);
        return "redirect:/admin/messages?doctorId=" + doctorId;
    }

    // ==========================================
    // DOCTOR MESSAGES
    // ==========================================
    @GetMapping("/doctor/messages")
    public String doctorMessages(Authentication authentication, Model model) {
        String docUsername = authentication.getName();
        var doctor = doctorService.findAllDoctors().stream()
                .filter(d -> d.getUsername().equals(docUsername))
                .findFirst()
                .orElseThrow();

        // Admin is "U-ADMIN"
        String adminId = "U-ADMIN";
        messageService.markAsRead(adminId, doctor.getDoctorId());
        
        List<Message> chat = messageService.getConversation(doctor.getDoctorId(), adminId);
        model.addAttribute("conversation", chat);
        model.addAttribute("doctor", doctor);
        return "member2-doctor-dashboard/messages";
    }

    @PostMapping("/doctor/messages/send")
    public String doctorSendMessage(Authentication authentication, @RequestParam String content) {
        String docUsername = authentication.getName();
        var doctor = doctorService.findAllDoctors().stream()
                .filter(d -> d.getUsername().equals(docUsername))
                .findFirst()
                .orElseThrow();
        
        String adminId = "U-ADMIN";
        messageService.sendMessage(doctor.getDoctorId(), adminId, content);
        return "redirect:/doctor/messages";
    }

    // ==========================================
    // PATIENT HELPDESK MESSAGES
    // ==========================================
    @GetMapping("/patient/helpdesk")
    public String patientHelpdesk(Authentication authentication, Model model) {
        String patientUsername = authentication.getName();
        var patient = userService.getUserRepository().findByUsername(patientUsername).orElseThrow();

        // Front desk user is "U-FD"
        String fdId = "U-FD";
        messageService.markAsRead(fdId, patient.getUserId());

        List<Message> chat = messageService.getConversation(patient.getUserId(), fdId);
        model.addAttribute("conversation", chat);
        model.addAttribute("patient", patient);
        return "member4-patient-dashboard/helpdesk";
    }

    @PostMapping("/patient/helpdesk/send")
    public String patientSendMessage(Authentication authentication, @RequestParam String content) {
        String patientUsername = authentication.getName();
        var patient = userService.getUserRepository().findByUsername(patientUsername).orElseThrow();

        String fdId = "U-FD";
        messageService.sendMessage(patient.getUserId(), fdId, content);
        return "redirect:/patient/helpdesk";
    }

    // ==========================================
    // FRONT DESK STAFF DASHBOARD & CHAT
    // ==========================================
    @GetMapping("/frontdesk/dashboard")
    public String frontdeskDashboard(Authentication authentication, 
                                     @RequestParam(required = false) String patientId, 
                                     Model model) {
        String fdUsername = authentication.getName();
        var fdUser = userService.getUserRepository().findByUsername(fdUsername).orElseThrow();

        model.addAttribute("patients", userService.getPatientRepository().findAll());
        model.addAttribute("selectedPatientId", patientId);
        model.addAttribute("frontdeskUser", fdUser);

        if (patientId != null && !patientId.isBlank()) {
            messageService.markAsRead(patientId, fdUser.getUserId());
            List<Message> chat = messageService.getConversation(fdUser.getUserId(), patientId);
            model.addAttribute("conversation", chat);
            
            var selectedPatient = userService.getPatientRepository().findAll().stream()
                    .filter(p -> p.getUserId().equals(patientId))
                    .findFirst()
                    .orElse(null);
            model.addAttribute("selectedPatient", selectedPatient);
        }
        return "member4-patient-dashboard/frontdesk-dashboard";
    }

    @PostMapping("/frontdesk/messages/send")
    public String frontdeskSendMessage(Authentication authentication, 
                                       @RequestParam String patientId, 
                                       @RequestParam String content) {
        String fdUsername = authentication.getName();
        var fdUser = userService.getUserRepository().findByUsername(fdUsername).orElseThrow();
        messageService.sendMessage(fdUser.getUserId(), patientId, content);
        return "redirect:/frontdesk/dashboard?patientId=" + patientId;
    }
}
