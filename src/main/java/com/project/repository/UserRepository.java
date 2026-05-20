package com.project.repository;

import com.project.model.Role;
import com.project.model.User;
import com.project.util.FileStorageManager;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository extends BaseFileRepository<UserRepository.SimpleUser> {

    public UserRepository(FileStorageManager fileStorageManager) {
        super("users.txt", fileStorageManager);
    }

    @Override
    protected SimpleUser mapFromLine(String line) {
        String[] p = line.split(",", -1);
        SimpleUser u = new SimpleUser(p[3], p[0], p[1], Role.valueOf(p[2]));
        if (p.length > 4) u.setFullName(p[4]);
        if (p.length > 5) u.setEmail(p[5]);
        if (p.length > 6) u.setPhone(p[6]);
        if (p.length > 7) u.setProfileImage(p[7]);
        return u;
    }

    @Override
    protected String mapToLine(SimpleUser item) {
        return String.join(",", 
            item.getUsername(), 
            item.getPasswordHash(), 
            item.getRole().name(), 
            item.getUserId(),
            item.getFullName() == null ? "" : item.getFullName(),
            item.getEmail() == null ? "" : item.getEmail(),
            item.getPhone() == null ? "" : item.getPhone(),
            item.getProfileImage() == null ? "" : item.getProfileImage()
        );
    }


    public Optional<SimpleUser> findByUsername(String username) {
        return findAll().stream().filter(u -> u.getUsername().equalsIgnoreCase(username)).findFirst();
    }

    public Optional<SimpleUser> findByUserId(String userId) {
        return findAll().stream().filter(u -> u.getUserId().equalsIgnoreCase(userId)).findFirst();
    }

    public void upsert(SimpleUser user) {
        List<SimpleUser> users = findAll();
        users.removeIf(u -> u.getUserId().equals(user.getUserId()));
        users.add(user);
        saveAll(users);
    }

    public static class SimpleUser extends User {
        public SimpleUser() {}
        public SimpleUser(String userId, String username, String passwordHash, Role role) {
            setUserId(userId);
            setUsername(username);
            setPasswordHash(passwordHash);
            setRole(role);
        }
    }
}
