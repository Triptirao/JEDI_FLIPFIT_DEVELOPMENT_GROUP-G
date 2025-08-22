package com.flipfit.dao;

import com.flipfit.bean.GymCentre;
import com.flipfit.bean.User;
import com.flipfit.utils.DBConnection;
import com.flipfit.exception.DAOException;
import com.flipfit.exception.UnableToDeleteUserException;

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
            // Wrap and re-throw SQLException as a custom DAOException
            throw new DAOException("Failed to retrieve pending gym requests.", e);
        }
        return pendingGyms;
    }

    public void approveGymRequest(int gymId) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(APPROVE_GYM)) {
            ps.setInt(1, gymId);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) {
                // If no rows were updated, the gymId might not exist.
                throw new DAOException("Could not approve gym. Gym with ID " + gymId + " not found.");
            }
        } catch (SQLException e) {
            // Wrap and re-throw SQLException
            throw new DAOException("Failed to approve gym request for ID: " + gymId, e);
        }
    }

    public List<User> getPendingGymOwnerRequests() {
        // This method relies on GymOwnerDAO, so its exception handling is in that class.
        return gymOwnerDao.getPendingGymOwners();
    }

    public void approveGymOwnerRequest(String email) {
        approveGymOwner(email);
    }

    public void approveGymOwner(String email) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(APPROVE_GYM_OWNER)) {
            ps.setString(1, email);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) {
                // If no rows were updated, the email might not exist.
                throw new DAOException("Could not approve gym owner. Email " + email + " not found.");
            }
        } catch (SQLException e) {
            // Wrap and re-throw SQLException
            throw new DAOException("Failed to approve gym owner request for email: " + email, e);
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
            // Wrap and re-throw SQLException
            throw new DAOException("Failed to retrieve all gyms.", e);
        }
        return allGyms;
    }

    public List<User> getAllGymOwners() {
        // This method relies on UserDAO, so its exception handling is in that class.
        return userDao.getAllGymOwners();
    }

    public List<User> getAllUsers() {
        // This method relies on UserDAO, so its exception handling is in that class.
        return userDao.getAllUsers();
    }

    public List<User> getAllCustomers() {
        // This method relies on UserDAO, so its exception handling is in that class.
        return userDao.getAllCustomers();
    }

    public void deleteUser(int userId) {
        Connection con = null;
        try {
            con = DBConnection.getConnection();
            con.setAutoCommit(false); // Start transaction

            // 1. Delete dependent records first to avoid foreign key constraint errors
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
                int rowsAffected = ps6.executeUpdate();
                if (rowsAffected == 0) {
                    throw new UnableToDeleteUserException("User with ID " + userId + " not found.");
                }
            }

            con.commit(); // Commit the transaction
        } catch (SQLException e) {
            try {
                if (con != null) {
                    con.rollback(); // Rollback on any failure to maintain data consistency
                }
            } catch (SQLException ex) {
                // Log or handle the rollback failure if necessary
                throw new UnableToDeleteUserException("Transaction rollback failed.", ex);
            }
            // Throw a specific exception for the deletion failure
            throw new UnableToDeleteUserException("Failed to delete user with ID: " + userId, e);
        } finally {
            if (con != null) {
                try {
                    con.close(); // Close the connection
                } catch (SQLException e) {
                    // Log or handle connection close failure
                    throw new DAOException("Failed to close database connection.", e);
                }
            }
        }
    }

    public void deleteGym(int gymId) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(DELETE_GYM)) {
            ps.setInt(1, gymId);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) {
                throw new DAOException("Could not delete gym. Gym with ID " + gymId + " not found.");
            }
        } catch (SQLException e) {
            // Wrap and re-throw SQLException
            throw new DAOException("Failed to delete gym with ID: " + gymId, e);
        }
    }

    private GymCentre mapResultSetToGymCentre(ResultSet rs) throws SQLException {
        try {
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
        } catch (SQLException e) {
            throw new DAOException("Failed to map ResultSet to GymCentre object.", e);
        }
    }
}
