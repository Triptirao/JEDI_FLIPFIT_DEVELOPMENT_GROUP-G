package com.flipfit.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class UserDAO {

    private static List<String[]> users = new ArrayList<>();
    private static final AtomicInteger userIdCounter = new AtomicInteger(0);

    static {
        // Dummy data for users: {Role, ID, Name, Email, Password, Phone, City, Pincode}
        users.add(new String[]{"ADMIN", "1", "Admin User", "admin@flipfit.com", "admin123", "9876543210", "New Delhi", "110001"});
    }

    public List<String[]> getAllUsers() {
        return users;
    }

    public Optional<String[]> getUserById(String userId) {
        return users.stream()
                .filter(user -> user[1].equals(userId))
                .findFirst();
    }

    public Optional<String[]> getUserByEmailAndPassword(String email, String password) {
        return users.stream()
                .filter(user -> user[3].equals(email) && user[4].equals(password))
                .findFirst();
    }

    public void deleteUser(String userId) {
        users.removeIf(user -> user[1].equals(userId));
    }

    public void addUser(String[] userData) {
        users.add(userData);
    }

    public String getNextUserId() {
        return String.valueOf(userIdCounter.incrementAndGet());
    }

    public void updateUserDetails(String userId, int choice, String newValue) {
        users.stream()
                .filter(user -> user[1].equals(userId))
                .findFirst()
                .ifPresent(user -> {
                    switch (choice) {
                        case 1: // Name
                            user[2] = newValue;
                            break;
                        case 2: // Email
                            user[3] = newValue;
                            break;
                        case 3: // Password
                            user[4] = newValue;
                            break;
                        case 4: // Phone Number
                            user[5] = newValue;
                            break;
                        default:
                            System.out.println("Invalid choice for update.");
                    }
                });
    }
}
