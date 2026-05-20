package com.project.repository;

import com.project.model.PatientRecord;
import com.project.util.FileStorageManager;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PatientRecordRepository extends BaseFileRepository<PatientRecord> {
    public PatientRecordRepository(FileStorageManager manager) { super("patient-records.txt", manager); }
    @Override protected PatientRecord mapFromLine(String line) {
        String[] p = line.split(",", -1);
        PatientRecord record = new PatientRecord();
        record.setRecordId(p[0]); record.setPatientId(p[1]); record.setDiagnosis(p[2]); record.setPrescriptions(p[3]); record.setNotes(p[4]); record.setVisitHistory(p[5]);
        return record;
    }
    @Override protected String mapToLine(PatientRecord record) {
        return String.join(",", record.getRecordId(), record.getPatientId(), record.getDiagnosis(), record.getPrescriptions(), record.getNotes(), record.getVisitHistory());
    }
    public void upsert(PatientRecord record) {
        List<PatientRecord> list = findAll();
        list.removeIf(r -> r.getRecordId().equals(record.getRecordId()));
        list.add(record);
        saveAll(list);
    }
}
