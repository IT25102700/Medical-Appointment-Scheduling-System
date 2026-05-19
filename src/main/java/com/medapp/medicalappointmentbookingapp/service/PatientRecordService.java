package com.medapp.medicalappointmentbookingapp.service;

import com.project.model.PatientRecord;
import com.project.repository.PatientRecordRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PatientRecordService {
    private final PatientRecordRepository repository;

    public PatientRecordService(PatientRecordRepository repository) {
        this.repository = repository;
    }

    public void save(PatientRecord record) {
        if (record.getRecordId() == null || record.getRecordId().isBlank()) {
            record.setRecordId("R-" + UUID.randomUUID());
        }
        repository.upsert(record);
    }

    public List<PatientRecord> findByPatient(String patientId) {
        return repository.findAll().stream().filter(r -> r.getPatientId().equals(patientId)).collect(Collectors.toList());
    }

    public List<PatientRecord> all() {
        return repository.findAll();
    }
}
