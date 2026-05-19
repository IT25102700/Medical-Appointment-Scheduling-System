package com.medapp.medicalappointmentbookingapp.repository;

import com.project.model.Admin;
import com.project.util.FileStorageManager;
import org.springframework.stereotype.Repository;

@Repository
public class AdminRepository extends BaseFileRepository<Admin> {
    public AdminRepository(FileStorageManager manager) { super("admins.txt", manager); }
    @Override protected Admin mapFromLine(String line) {
        String[] p = line.split(",", -1);
        return new Admin(p[0], p[1], "", p[2], p[3], p[4], p[5]);
    }
    @Override protected String mapToLine(Admin a) {
        return String.join(",", a.getUserId(), a.getUsername(), a.getFullName(), a.getEmail(), a.getPhone(), a.getDepartment());
    }
}
