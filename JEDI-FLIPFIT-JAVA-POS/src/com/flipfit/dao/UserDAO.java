package com.flipfit.dao;

import com.flipfit.bean.Customer;
import com.flipfit.bean.User;
import com.flipfit.utils.DBConnection;

import java.sql.*;
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

    public int addUser(User user) {
        int generatedId = -1;
        String sql = "INSERT INTO `user` (`fullName`, `email`, `password`, `userPhone`, `city`, `pincode`, `roleId`) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getPassword());
            pstmt.setLong(4, user.getUserPhone());
            pstmt.setString(5, user.getCity());
            pstmt.setInt(6, user.getPinCode());
            pstmt.setInt(7, user.getId());

            // Execute the update
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                // Retrieve the generated key
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        // The first column in the ResultSet is the generated key
                        generatedId = generatedKeys.getInt(1);
                    }
                }
            }

            return generatedId;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return  generatedId;
    }

    public boolean addCustomer(Customer customer) {
        String sql = "INSERT INTO `customer` (`userId`, `paymentType`, `paymentInfo`) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, customer.getUserId());
            pstmt.setInt(2, customer.getPaymentType());
            pstmt.setString(3, customer.getPaymentInfo());

            // Execute the update
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return  false;
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
