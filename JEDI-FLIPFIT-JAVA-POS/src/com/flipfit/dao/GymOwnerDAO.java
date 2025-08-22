package com.flipfit.dao;

import com.flipfit.bean.GymCentre;
import com.flipfit.bean.GymOwner;
import com.flipfit.bean.User;
import com.flipfit.exception.DAOException;
import com.flipfit.exception.MissingValueException;
import com.flipfit.utils.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * The {@code GymOwnerDAO} class provides **Data Access Object (DAO)**
 * functionality for managing **GymOwner** and their associated **GymCentre** entities
 * in the FlipFit system.
 * <p>
 * This class handles database operations such as creating new gym owners and
 * gym centers, retrieving their details, and managing the approval process.
 * It also includes methods to fetch data relevant to gym owners and administrators.
 * </p>
 *
 * @author YourName
 * @version 1.0
 * @since 2025-08-22
 */
public class GymOwnerDAO {

    /**
     * Data Access Object for managing User entities.
     */
    private UserDAO userDao = new UserDAO();

    // SQL queries for various database operations.
    private static final String INSERT_GYM_OWNER = "INSERT INTO `GymOwner` (ownerId, pan, aadhaar, gst) VALUES (?, ?, ?, ?)";
    private static final String SELECT_GYM_OWNER_BY_ID = "SELECT * FROM `GymOwner` WHERE ownerId = ?";
    private static final String SELECT_PENDING_GYMS = "SELECT * FROM GymCentre WHERE approved = FALSE";
    private static final String APPROVE_GYM = "UPDATE GymCentre SET approved = TRUE WHERE centreId = ?";
    private static final String DELETE_GYM = "DELETE FROM GymCentre WHERE centreId = ?";
    private static final String SELECT_ALL_APPROVED_GYMS = "SELECT * FROM GymCentre WHERE approved = TRUE";
    private static final String SELECT_ALL_GYMS = "SELECT * FROM GymCentre";
    private static final String INSERT_GYM_CENTRE = "INSERT INTO GymCentre (ownerId, name, capacity, approved, city, state, pincode) VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static final String SELECT_GYMS_BY_OWNER_ID = "SELECT * FROM GymCentre WHERE ownerId = ?";
    private static final String UPDATE_GYM_OWNER_DETAILS = "UPDATE GymOwner SET pan = ?, aadhaar = ?, gst = ? WHERE ownerId = ?";
    private static final String SELECT_PENDING_GYM_OWNERS = "SELECT u.* FROM User u JOIN GymOwner go ON u.userId = go.ownerId WHERE go.isApproved = FALSE";

    /**
     * Retrieves a {@code GymOwner} by their user ID.
     *
     * @param userId The ID of the user which is also the gym owner's ID.
     * @return An {@link Optional} containing the {@code GymOwner} if found, otherwise an empty Optional.
     * @throws DAOException if a database access error occurs.
     */
    public Optional<GymOwner> getGymOwnerById(int userId) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_GYM_OWNER_BY_ID)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Map the ResultSet to a GymOwner object
                    GymOwner gymOwner = new GymOwner(
                            rs.getInt("ownerId"),
                            rs.getString("pan"),
                            rs.getString("aadhaar"),
                            rs.getString("gst"),
                            rs.getBoolean("isApproved")
                    );
                    return Optional.of(gymOwner);
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Failed to retrieve gym owner by ID: " + userId, e);
        }
        return Optional.empty();
    }

    /**
     * Adds a new gym center to the database.
     *
     * @param gym The {@code GymCentre} object to be added.
     * @throws DAOException if a database access error occurs.
     */
    public void addGym(GymCentre gym) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(INSERT_GYM_CENTRE)) {
            ps.setInt(1, gym.getOwnerId());
            ps.setString(2, gym.getCentreName());
            ps.setInt(3, gym.getCapacity());
            ps.setBoolean(4, gym.isApproved());
            ps.setString(5, gym.getCity());
            ps.setString(6, gym.getState());
            ps.setString(7, gym.getPincode());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Failed to add gym center.", e);
        }
    }

    /**
     * Retrieves a list of all gym centers owned by a specific gym owner.
     *
     * @param ownerId The ID of the gym owner.
     * @return A {@link List} of {@code GymCentre} objects.
     * @throws DAOException if a database access error occurs.
     */
    public List<GymCentre> getGymsByOwnerId(int ownerId) {
        List<GymCentre> gyms = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_GYMS_BY_OWNER_ID)) {
            ps.setInt(1, ownerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    gyms.add(mapResultSetToGymCentre(rs));
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Failed to retrieve gyms for owner ID: " + ownerId, e);
        }
        return gyms;
    }

    /**
     * Retrieves a list of all customers from the system.
     *
     * @return A {@link List} of {@code User} objects with the customer role.
     */
    public List<User> getAllCustomers() {
        return userDao.getAllCustomers();
    }

    /**
     * Retrieves payment information for a specific gym.
     * This is a placeholder method.
     *
     * @param gymId The ID of the gym.
     * @return An empty {@link List} of String arrays.
     */
    public List<String[]> getPaymentsByGym(int gymId) {
        // This is a placeholder and would require a separate `PaymentDAO` in a real application.
        return new ArrayList<>();
    }

    /**
     * Adds a new gym owner to the database.
     *
     * @param gymOwner The {@code GymOwner} object to be added.
     * @throws DAOException if a database access error occurs.
     */
    public void addGymOwner(GymOwner gymOwner) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(INSERT_GYM_OWNER)) {
            ps.setInt(1, gymOwner.getUserId());
            ps.setString(2, gymOwner.getPan());
            ps.setString(3, gymOwner.getAadhaar());
            ps.setString(4, gymOwner.getGst());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Failed to add gym owner.", e);
        }
    }

    /**
     * Retrieves a list of all gym centers from the database.
     *
     * @return A {@link List} of all {@code GymCentre} objects.
     * @throws DAOException if a database access error occurs.
     */
    public List<GymCentre> getAllGyms() {
        List<GymCentre> gyms = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_ALL_GYMS);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                gyms.add(mapResultSetToGymCentre(rs));
            }
        } catch (SQLException e) {
            throw new DAOException("Failed to retrieve all gyms.", e);
        }
        return gyms;
    }

    /**
     * Updates the details of an existing gym owner in the database.
     *
     * @param gymOwner The {@code GymOwner} object containing the updated details.
     * @throws DAOException if a database access error occurs or if the gym owner is not found.
     */
    public void updateGymOwnerDetails(GymOwner gymOwner) {
        // Update the GymOwner table for specific details
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(UPDATE_GYM_OWNER_DETAILS)) {
            ps.setString(1, gymOwner.getPan());
            ps.setString(2, gymOwner.getAadhaar());
            ps.setString(3, gymOwner.getGst());
            ps.setInt(4, gymOwner.getUserId());
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) {
                throw new DAOException("Could not update gym owner. Gym owner with ID " + gymOwner.getUserId() + " not found.");
            }
        } catch (SQLException e) {
            throw new DAOException("Failed to update gym owner details for ID: " + gymOwner.getUserId(), e);
        }
    }


    /**
     * Retrieves a list of gym centers that are awaiting approval.
     *
     * @return A {@link List} of {@code GymCentre} objects that are not yet approved.
     * @throws DAOException if a database access error occurs.
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
     * Approves a gym center, updating its status to approved in the database.
     *
     * @param gymId The ID of the gym to approve.
     * @throws DAOException if a database access error occurs or if the gym is not found.
     */
    public void approveGym(int gymId) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(APPROVE_GYM)) {
            ps.setInt(1, gymId);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) {
                throw new DAOException("Could not approve gym. Gym with ID " + gymId + " not found.");
            }
        } catch (SQLException e) {
            throw new DAOException("Failed to approve gym with ID: " + gymId, e);
        }
    }

    /**
     * Deletes a gym center from the database.
     *
     * @param gymId The ID of the gym to delete.
     * @throws DAOException if a database access error occurs or if the gym is not found.
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
     * Retrieves a list of all approved gym centers from the database.
     *
     * @return A {@link List} of approved {@code GymCentre} objects.
     * @throws DAOException if a database access error occurs.
     */
    public List<GymCentre> getApprovedGyms() {
        List<GymCentre> approvedGyms = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_ALL_APPROVED_GYMS);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                approvedGyms.add(mapResultSetToGymCentre(rs));
            }
        } catch (SQLException e) {
            throw new DAOException("Failed to retrieve approved gyms.", e);
        }
        return approvedGyms;
    }

    /**
     * Retrieves a list of gym owners who are awaiting administrative approval.
     *
     * @return A {@link List} of {@code User} objects representing the pending gym owners.
     * @throws DAOException if a database access error occurs.
     */
    public List<User> getPendingGymOwners() {
        List<User> pendingOwners = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_PENDING_GYM_OWNERS);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                pendingOwners.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            throw new DAOException("Failed to retrieve pending gym owners.", e);
        }
        return pendingOwners;
    }

    /**
     * Helper method to map a {@link ResultSet} row to a {@code GymCentre} object.
     *
     * @param rs The {@link ResultSet} containing gym centre data.
     * @return A new {@code GymCentre} object.
     * @throws SQLException if a database access error occurs.
     * @throws DAOException if the mapping fails.
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

    /**
     * Helper method to map a {@link ResultSet} row to a {@code User} object.
     *
     * @param rs The {@link ResultSet} containing user data.
     * @return A new {@code User} object.
     * @throws SQLException if a database access error occurs.
     * @throws DAOException if the mapping fails.
     */
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        try {
            return new User(
                    rs.getInt("userId"),
                    rs.getString("fullName"),
                    rs.getString("email"),
                    rs.getString("password"),
                    rs.getLong("userPhone"),
                    rs.getString("city"),
                    rs.getInt("pinCode")
            );
        } catch (SQLException e) {
            throw new DAOException("Failed to map ResultSet to User object.", e);
        }
    }
}