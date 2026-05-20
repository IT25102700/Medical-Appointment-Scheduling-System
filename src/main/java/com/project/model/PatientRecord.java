package com.project.model;

public class PatientRecord {
    private String recordId;
    private String patientId;
    private String diagnosis;
    private String prescriptions;
    private String notes;
    private String visitHistory;

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getPrescriptions() {
        return prescriptions;
    }

    public void setPrescriptions(String prescriptions) {
        this.prescriptions = prescriptions;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getVisitHistory() {
        return visitHistory;
    }

    public void setVisitHistory(String visitHistory) {
        this.visitHistory = visitHistory;
    }
}
