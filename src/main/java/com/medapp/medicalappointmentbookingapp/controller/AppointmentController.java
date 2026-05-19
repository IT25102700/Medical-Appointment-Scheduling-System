package com.medapp.medicalappointmentbookingapp.controller;

import com.project.model.Appointment;
import com.project.model.AppointmentStatus;
import com.project.service.AppointmentService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/appointments")
public class AppointmentController {
    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @GetMapping
    public String listAppointments(Model model) {
        List<Appointment> appointments = appointmentService.all();
        model.addAttribute("appointments", appointments);
        return "appointment/list"; // JSP view
    }

    @PostMapping("/book")
    public String bookAppointment(@RequestParam String patientId, 
                                  @RequestParam String doctorId, 
                                  @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime time, 
                                  @RequestParam String notes) {
        appointmentService.book(patientId, doctorId, time, notes);
        return "redirect:/appointments";
    }

    @PostMapping("/updateStatus")
    public String updateStatus(@RequestParam String appointmentId, 
                               @RequestParam AppointmentStatus status) {
        appointmentService.updateStatus(appointmentId, status);
        return "redirect:/appointments";
    }

    @PostMapping("/reschedule")
    public String rescheduleAppointment(@RequestParam String appointmentId, 
                                        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime newTime) {
        appointmentService.reschedule(appointmentId, newTime);
        return "redirect:/appointments";
    }
}
