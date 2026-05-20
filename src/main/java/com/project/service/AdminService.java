package com.project.service;

import com.project.model.AppointmentStatus;
import com.project.model.PaymentState;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AdminService {
    private final UserService userService;
    private final DoctorService doctorService;
    private final AppointmentService appointmentService;
    private final PaymentService paymentService;

    public AdminService(UserService userService, DoctorService doctorService, AppointmentService appointmentService, PaymentService paymentService) {
        this.userService = userService;
        this.doctorService = doctorService;
        this.appointmentService = appointmentService;
        this.paymentService = paymentService;
    }

    public Map<String, Object> dashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("users", userService.getUserRepository().findAll().size());
        stats.put("doctors", doctorService.findAllDoctors().size());
        stats.put("appointments", appointmentService.all().size());
        stats.put("booked", appointmentService.all().stream().filter(a -> a.getStatus() == AppointmentStatus.BOOKED).count());
        stats.put("cancelled", appointmentService.all().stream().filter(a -> a.getStatus() == AppointmentStatus.CANCELLED).count());
        stats.put("paid", paymentService.history().stream().filter(p -> p.getState() == PaymentState.PAID).count());
        stats.put("refunded", paymentService.history().stream().filter(p -> p.getState() == PaymentState.REFUNDED).count());
        return stats;
    }
}
