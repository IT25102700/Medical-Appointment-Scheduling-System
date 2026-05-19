package com.medapp.medicalappointmentbookingapp.repository;

import com.project.model.Payment;
import com.project.model.PaymentState;
import com.project.util.FileStorageManager;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PaymentRepository extends BaseFileRepository<Payment> {
    public PaymentRepository(FileStorageManager manager) { super("payments.txt", manager); }
    @Override protected Payment mapFromLine(String line) {
        String[] p = line.split(",", -1);
        Payment payment = new Payment();
        payment.setPaymentId(p[0]); payment.setAppointmentId(p[1]); payment.setAmount(Double.parseDouble(p[2]));
        payment.setMethod(p[3]); payment.setState(PaymentState.valueOf(p[4]));
        return payment;
    }
    @Override protected String mapToLine(Payment payment) {
        return String.join(",", payment.getPaymentId(), payment.getAppointmentId(), String.valueOf(payment.getAmount()), payment.getMethod(), payment.getState().name());
    }
    public void upsert(Payment payment) {
        List<Payment> list = findAll();
        list.removeIf(p -> p.getPaymentId().equals(payment.getPaymentId()));
        list.add(payment);
        saveAll(list);
    }
}
