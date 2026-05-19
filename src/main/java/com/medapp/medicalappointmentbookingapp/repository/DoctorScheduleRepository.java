package com.medapp.medicalappointmentbookingapp.repository;

import com.project.model.DoctorSchedule;
import com.project.util.FileStorageManager;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DoctorScheduleRepository extends BaseFileRepository<DoctorSchedule> {
    public DoctorScheduleRepository(FileStorageManager manager) { super("doctor-schedules.txt", manager); }
    @Override protected DoctorSchedule mapFromLine(String line) {
        String[] p = line.split(",", -1);
        DoctorSchedule schedule = new DoctorSchedule();
        schedule.setScheduleId(p[0]); schedule.setDoctorId(p[1]); schedule.setDayOfWeek(p[2]); schedule.setStartTime(p[3]); schedule.setEndTime(p[4]);
        return schedule;
    }
    @Override protected String mapToLine(DoctorSchedule schedule) {
        return String.join(",", schedule.getScheduleId(), schedule.getDoctorId(), schedule.getDayOfWeek(), schedule.getStartTime(), schedule.getEndTime());
    }
    public void upsert(DoctorSchedule schedule) {
        List<DoctorSchedule> list = findAll();
        list.removeIf(s -> s.getScheduleId().equals(schedule.getScheduleId()));
        list.add(schedule);
        saveAll(list);
    }
}
