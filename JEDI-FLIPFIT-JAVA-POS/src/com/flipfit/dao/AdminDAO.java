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

/**
 * Data Access Object (DAO) class for managing Admin-level operations in the FlipFit application.
 * This class handles database interactions related to administrative tasks such as
 * approving gyms, deleting users, and viewing pending requests.
 * * @author YourName
 */
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

    /**
     * Constructs an AdminDAO with necessary DAO dependencies.
     * @param userDao The DAO for User operations.
     * @param customerDao The DAO for Customer operations.
     * @param gymOwnerDao The DAO for GymOwner operations.
     */
    public AdminDAO(UserDAO userDao, CustomerDAO customerDao, GymOwnerDAO gymOwnerDao) {
        this.userDao = userDao;
        this.customerDao = customerDao;
        this.gymOwnerDao = gymOwnerDao;
    }

    /**
     * Retrieves a list of gym centers that are awaiting admin approval.
     * @return A list of GymCentre objects with `approved` set to false.
     * @throws DAOException If a database access error occurs.
     */
    public List<GymCentre> getPendingGymRequests() {
        List<GymCentre> pendingGyms = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_PENDING_GYMS);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                pendingGyms.add(mapResultSetToGymCentre(rs));
            }
        } catch (SQLException e) {
            throw new DAOException("Failed to retrieve pending gym requests.", e);
        }
        return pendingGyms;
    }

    /**
     * Approves a specific gym center by setting its `approved` status to true.
     * @param gymId The unique identifier of the gym to be approved.
     * @throws DAOException If the gym cannot be approved, or if the gymId does not exist.
     */
    public void approveGymRequest(int gymId) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(APPROVE_GYM)) {
            ps.setInt(1, gymId);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) {
                throw new DAOException("Could not approve gym. Gym with ID " + gymId + " not found.");
            }
        } catch (SQLException e) {
            throw new DAOException("Failed to approve gym request for ID: " + gymId, e);
        }
    }

    /**
     * Retrieves a list of gym owners who are awaiting admin approval.
     * This method delegates the call to the GymOwnerDAO.
     * @return A list of User objects representing pending gym owners.
     */
    public List<User> getPendingGymOwnerRequests() {
        return gymOwnerDao.getPendingGymOwners();
    }

    /**
     * Approves a gym owner's registration.
     * @param email The email of the gym owner to be approved.
     */
    public void approveGymOwnerRequest(String email) {
        approveGymOwner(email);
    }

    /**
     * Approves a gym owner in the database.
     * @param email The email of the gym owner.
     * @throws DAOException If a database access error occurs or the email is not found.
     */
    public void approveGymOwner(String email) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(APPROVE_GYM_OWNER)) {
            ps.setString(1, email);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) {
                throw new DAOException("Could not approve gym owner. Email " + email + " not found.");
            }
        } catch (SQLException e) {
            throw new DAOException("Failed to approve gym owner request for email: " + email, e);
        }
    }

    /**
     * Retrieves a list of all gym centers.
     * @return A list of all GymCentre objects.
     * @throws DAOException If a database access error occurs.
     */
    public List<GymCentre> getAllGyms() {
        List<GymCentre> allGyms = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_ALL_GYMS);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                allGyms.add(mapResultSetToGymCentre(rs));
            }
        } catch (SQLException e) {
            throw new DAOException("Failed to retrieve all gyms.", e);
        }
        return allGyms;
    }

    /**
     * Retrieves a list of all approved gym owners.
     * This method delegates the call to the UserDAO.
     * @return A list of User objects representing gym owners.
     */
    public List<User> getAllGymOwners() {
        return userDao.getAllGymOwners();
    }

    /**
     * Retrieves a list of all registered users.
     * This method delegates the call to the UserDAO.
     * @return A list of all User objects.
     */
    public List<User> getAllUsers() {
        return userDao.getAllUsers();
    }

    /**
     * Retrieves a list of all registered customers.
     * This method delegates the call to the UserDAO.
     * @return A list of all User objects representing customers.
     */
    public List<User> getAllCustomers() {
        return userDao.getAllCustomers();
    }

    /**
     * Deletes a user and all their associated data in a single transaction.
     * This method ensures data integrity by deleting dependent records first.
     * @param userId The unique identifier of the user to be deleted.
     * @throws UnableToDeleteUserException If the user cannot be deleted, or if the transaction fails.
     * @throws DAOException If the connection cannot be closed.
     */
    public void deleteUser(int userId) {
        Connection con = null;
        try {
            con = DBConnection.getConnection();
            con.setAutoCommit(false); // Start transaction

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
                    con.rollback();
                }
            } catch (SQLException ex) {
                throw new UnableToDeleteUserException("Transaction rollback failed.", ex);
            }
            throw new UnableToDeleteUserException("Failed to delete user with ID: " + userId, e);
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    throw new DAOException("Failed to close database connection.", e);
                }
            }
        }
    }

    /**
     * Deletes a gym center from the database.
     * @param gymId The unique identifier of the gym to be deleted.
     * @throws DAOException If the gym cannot be deleted, or if the gymId does not exist.
     */
    public void deleteGym(int gymId) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(DELETE_GYM)) {
            ps.setInt(1, gymId);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) {
                throw new DAOException("Could not delete gym. Gym with ID " + gymId + " not found.");
            }
        } catch (SQLException e) {
            throw new DAOException("Failed to delete gym with ID: " + gymId, e);
        }
    }

    /**
     * Maps a ResultSet row to a GymCentre object.
     * @param rs The ResultSet containing the gym data.
     * @return A GymCentre object populated with data from the ResultSet.
     * @throws SQLException If a column is missing from the ResultSet.
     */
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
