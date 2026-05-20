package com.project.controller;

import com.project.model.AppointmentStatus;
import com.project.service.AppointmentService;
import com.project.service.DoctorService;
import com.project.service.PaymentService;
import com.project.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;

@Controller
public class AppointmentController {
    private final AppointmentService appointmentService;
    private final DoctorService doctorService;
    private final UserService userService;
    private final PaymentService paymentService;

    public AppointmentController(AppointmentService appointmentService, DoctorService doctorService, UserService userService, PaymentService paymentService) {
        this.appointmentService = appointmentService;
        this.doctorService = doctorService;
        this.userService = userService;
        this.paymentService = paymentService;
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
    public String book(Authentication authentication,
                       @RequestParam String doctorId,
                       @RequestParam String date,
                       @RequestParam String time,
                       @RequestParam(defaultValue = "") String notes,
                       Model model) {
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
        return "member4-patient-dashboard/checkout";
    }

    @PostMapping("/patient/cancel")
    public String cancel(@RequestParam String appointmentId) {
        appointmentService.updateStatus(appointmentId, AppointmentStatus.CANCELLED);
        paymentService.refundByAppointment(appointmentId);
        return "redirect:/patient/appointments";
    }

    @GetMapping("/patient/reschedule")
    public String rescheduleView(@RequestParam String appointmentId, Model model) {
        var appt = appointmentService.findById(appointmentId);
        model.addAttribute("appointment", appt);
        model.addAttribute("doctor", doctorService.findAllDoctors().stream()
                .filter(d -> d.getDoctorId().equals(appt.getDoctorId()))
                .findFirst()
                .orElse(null));
        return "member6-appointment-booking/reschedule";
    }

    @PostMapping("/patient/reschedule")
    public String reschedule(@RequestParam String appointmentId,
                             @RequestParam String date,
                             @RequestParam String time) {
        LocalDateTime ldt = LocalDateTime.now();
        try {
            int hour = Integer.parseInt(time.split(":")[0]);
            int min = Integer.parseInt(time.split(":")[1].split(" ")[0]);
            if (time.contains("PM") && hour < 12) hour += 12;
            if (time.contains("AM") && hour == 12) hour = 0;
            ldt = LocalDateTime.of(java.time.LocalDate.parse(date), java.time.LocalTime.of(hour, min));
        } catch (Exception e) {}
        appointmentService.reschedule(appointmentId, ldt);
        return "redirect:/patient/appointments";
    }
}
