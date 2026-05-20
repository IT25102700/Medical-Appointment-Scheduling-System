package com.project.model;

public class Admin extends User {
    private String department;

    public Admin() {
        setRole(Role.ROLE_ADMIN);
    }

    public Admin(String userId, String username, String passwordHash, String fullName, String email, String phone, String department) {
        super(userId, username, passwordHash, Role.ROLE_ADMIN, fullName, email, phone);
        this.department = department;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }
}
