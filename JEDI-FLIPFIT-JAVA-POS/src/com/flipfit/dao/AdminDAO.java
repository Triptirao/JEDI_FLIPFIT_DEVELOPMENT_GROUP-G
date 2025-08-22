package com.flipfit.dao;

import com.flipfit.bean.GymCentre;
import com.flipfit.bean.User;
import com.flipfit.utils.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AdminDAO {

    private final UserDAO userDao;
    private final GymOwnerDAO gymOwnerDao;
    private final CustomerDAO customerDao;

    // SQL queries for GymCentre operations
    private static final String SELECT_PENDING_GYMS = "SELECT * FROM GymCentre WHERE approved = FALSE";
    private static final String SELECT_ALL_GYMS = "SELECT * FROM GymCentre";
    private static final String APPROVE_GYM = "UPDATE GymCentre SET approved = TRUE WHERE centreId = ?";
    private static final String DELETE_GYM = "DELETE FROM GymCentre WHERE centreId = ?";
    private static final String APPROVE_GYM_OWNER = "UPDATE `GymOwner` SET isApproved = TRUE WHERE ownerId = (SELECT userId FROM `User` WHERE email = ?)";
    // ... (other fields and SQL queries)
    private static final String DELETE_BOOKINGS_BY_USER_ID = "DELETE FROM Booking WHERE customerId = ?";
    private static final String DELETE_CUSTOMER_BY_USER_ID = "DELETE FROM Customer WHERE customerId = ?";
    private static final String DELETE_GYMS_BY_OWNER_ID = "DELETE FROM GymCentre WHERE ownerId = ?";
    private static final String DELETE_GYM_OWNER_BY_USER_ID = "DELETE FROM GymOwner WHERE ownerId = ?";
    private static final String DELETE_ADMIN_BY_USER_ID = "DELETE FROM Admin WHERE adminId = ?";
    private static final String DELETE_USER = "DELETE FROM User WHERE userId = ?";

    // The corrected constructor now takes all necessary dependencies
    public AdminDAO(UserDAO userDao, CustomerDAO customerDao, GymOwnerDAO gymOwnerDao) {
        this.userDao = userDao;
        this.customerDao = customerDao;
        this.gymOwnerDao = gymOwnerDao;
    }

    public List<GymCentre> getPendingGymRequests() {
        List<GymCentre> pendingGyms = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_PENDING_GYMS);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                pendingGyms.add(mapResultSetToGymCentre(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pendingGyms;
    }

    public void approveGymRequest(int gymId) {
        // The query is updated to be a valid prepared statement with one placeholder
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(APPROVE_GYM)) {
            // Only set the gymId parameter, as 'TRUE' is hardcoded into the query
            ps.setInt(1, gymId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<User> getPendingGymOwnerRequests() {
        return gymOwnerDao.getPendingGymOwners();
    }

    public void approveGymOwnerRequest(String email) {
        approveGymOwner(email);
    }
    public void approveGymOwner(String email) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(APPROVE_GYM_OWNER)) {
            ps.setString(1, email);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public List<GymCentre> getAllGyms() {
        List<GymCentre> allGyms = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_ALL_GYMS);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                allGyms.add(mapResultSetToGymCentre(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return allGyms;
    }

    public List<User> getAllGymOwners() {
        return userDao.getAllGymOwners();
    }

    public List<User> getAllUsers() {
        return userDao.getAllUsers();
    }

    public List<User> getAllCustomers() {
        return userDao.getAllCustomers();
    }


    public void deleteUser(int userId) {
        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false); // Start transaction

            // 1. Delete dependent records from child tables first
            try (PreparedStatement ps1 = con.prepareStatement(DELETE_BOOKINGS_BY_USER_ID)) {
                ps1.setInt(1, userId);
                ps1.executeUpdate();
            }
            try (PreparedStatement ps2 = con.prepareStatement(DELETE_CUSTOMER_BY_USER_ID)) {
                ps2.setInt(1, userId);
                ps2.executeUpdate();
            }
            try (PreparedStatement ps3 = con.prepareStatement(DELETE_GYMS_BY_OWNER_ID)) {
                ps3.setInt(1, userId);
                ps3.executeUpdate();
            }
            try (PreparedStatement ps4 = con.prepareStatement(DELETE_GYM_OWNER_BY_USER_ID)) {
                ps4.setInt(1, userId);
                ps4.executeUpdate();
            }
            try (PreparedStatement ps5 = con.prepareStatement(DELETE_ADMIN_BY_USER_ID)) {
                ps5.setInt(1, userId);
                ps5.executeUpdate();
            }

            // 2. Finally, delete the record from the parent User table
            try (PreparedStatement ps6 = con.prepareStatement(DELETE_USER)) {
                ps6.setInt(1, userId);
                ps6.executeUpdate();
            }

            con.commit(); // Commit the transaction if all operations were successful
        } catch (SQLException e) {
            e.printStackTrace();
            // Rollback is implicitly handled by the try-with-resources block's closing
        }
    }

    public void deleteGym(int gymId) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(DELETE_GYM)) {
            ps.setInt(1, gymId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private GymCentre mapResultSetToGymCentre(ResultSet rs) throws SQLException {
        return new GymCentre(
                rs.getInt("centreId"),
                rs.getInt("ownerId"),
                rs.getString("name"),
                rs.getString("slots"),
                rs.getInt("capacity"),
                rs.getBoolean("approved"),
                rs.getString("city"),
                rs.getString("state"),
                rs.getString("pincode"),
                rs.getString("facilities")
        );
    }
}