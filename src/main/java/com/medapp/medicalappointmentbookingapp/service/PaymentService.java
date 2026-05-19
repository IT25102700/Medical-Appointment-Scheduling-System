package com.medapp.medicalappointmentbookingapp.service;

import com.project.model.Payment;
import com.project.model.PaymentState;
import com.project.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PaymentService {
    private final PaymentRepository repository;

    public PaymentService(PaymentRepository repository) {
        this.repository = repository;
    }

    public Payment submit(String appointmentId, double amount, String method) {
        Payment payment = new Payment();
        payment.setPaymentId("P-" + UUID.randomUUID());
        payment.setAppointmentId(appointmentId);
        payment.setAmount(amount);
        payment.setMethod(method);
        payment.setState(PaymentState.PAID);
        repository.append(payment);
        return payment;
    }

    public void refundByAppointment(String appointmentId) {
        repository.findAll().stream()
                .filter(p -> p.getAppointmentId().equals(appointmentId))
                .findFirst()
                .ifPresent(p -> {
                    p.setState(PaymentState.REFUNDED);
                    repository.upsert(p);
                });
    }

    public List<Payment> history() {
        return repository.findAll();
    }

    public List<Payment> forAppointment(String appointmentId) {
        return repository.findAll().stream().filter(p -> p.getAppointmentId().equals(appointmentId)).collect(Collectors.toList());
    }
}
