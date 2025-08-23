package com.flipfit.dao;

import com.flipfit.bean.*;
import com.flipfit.exception.DAOException;
import com.flipfit.exception.MissingValueException;
import com.flipfit.utils.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

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

    // SQL queries for various database operations.
    private static final String INSERT_GYM_OWNER = "INSERT INTO `GymOwner` (ownerId, pan, aadhaar, gst) VALUES (?, ?, ?, ?)";
    private static final String SELECT_GYM_OWNER_BY_ID = "SELECT * FROM `GymOwner` WHERE ownerId = ?";
    private static final String INSERT_GYM_CENTRE = "INSERT INTO GymCentre (ownerId, name, capacity, cost, approved, city, state, pincode, facilities) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String SELECT_GYMS_BY_OWNER_ID = "SELECT * FROM GymCentre WHERE ownerId = ?";
    private static final String SELECT_GYM_BY_OWNER_ID_AND_GYM_ID = "SELECT * FROM GymCentre WHERE ownerId = ? AND centreId = ? AND approved = TRUE";
    private static final String UPDATE_GYM_OWNER_DETAILS = "UPDATE GymOwner SET pan = ?, aadhaar = ?, gst = ? WHERE ownerId = ?";
    private static final String INSERT_SLOT_DETAILS = "INSERT INTO Slot (gymId, startTime, endTime, capacity, bookedCount) VALUES (?, ?, ?, ?, ?)";
    private static final String SELECT_BOOKINGS_BY_GYM_ID = "SELECT * FROM Booking WHERE gymId = ?";
    private static final String SELECT_GYM_BY_GYM_ID = "SELECT * FROM GymCentre WHERE centreId = ?";

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
    public int addGym(GymCentre gym) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(INSERT_GYM_CENTRE, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, gym.getOwnerId());
            ps.setString(2, gym.getCentreName());
            ps.setInt(3, gym.getCapacity());
            ps.setInt(4, gym.getCost());
            ps.setBoolean(5, gym.isApproved());
            ps.setString(6, gym.getCity());
            ps.setString(7, gym.getState());
            ps.setString(8, gym.getPincode());
            ps.setString(9, gym.getFacilities());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Failed to add gym center.", e);
        }
        return -1;
    }

    /**
     * Adds new slots corresponding to gym
     *
     * @param slot The {@code Slot} object to be added.
     * @throws DAOException if a database access error occurs.
     */
    public void addSlots(Slot slot) {
        List<Map.Entry<LocalTime, LocalTime>> slots = List.of(
                new AbstractMap.SimpleImmutableEntry<>(LocalTime.of(6, 0), LocalTime.of(7, 0)),
                new AbstractMap.SimpleImmutableEntry<>(LocalTime.of(7, 0), LocalTime.of(8, 0)),
                new AbstractMap.SimpleImmutableEntry<>(LocalTime.of(8, 0), LocalTime.of(9, 0)),
                new AbstractMap.SimpleImmutableEntry<>(LocalTime.of(19, 0), LocalTime.of(20, 0)),
                new AbstractMap.SimpleImmutableEntry<>(LocalTime.of(20, 0), LocalTime.of(21, 0)),
                new AbstractMap.SimpleImmutableEntry<>(LocalTime.of(21, 0), LocalTime.of(22, 0))
        );

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(INSERT_SLOT_DETAILS)) {

            // Loop through each slot and insert it into the database
            for (Map.Entry<LocalTime, LocalTime> newSlot : slots) {
                ps.setInt(1, slot.getGymId());
                ps.setTime(2, Time.valueOf(newSlot.getKey()));
                ps.setTime(3, Time.valueOf(newSlot.getValue()));
                ps.setInt(4, slot.getCapacity());
                ps.setInt(5, 0); // The booked count is always 0 for a new slot

                ps.addBatch();
            }

            ps.executeBatch();

        } catch (SQLException e) {
            throw new DAOException("Failed to add gym slots.", e);
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
     * Retrieves a list of all bookings for a particular gym.
     *
     * @return A {@link List} of {@code Booking} objects.
     */
    public List<Booking> getAllBookingsByGymId(int gymId) {
        List<Booking> bookings = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_BOOKINGS_BY_GYM_ID)) {
            ps.setInt(1, gymId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    bookings.add(mapResultSetToBooking(rs));
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Failed to retrieve booking for gym ID: " + gymId, e);
        }
        return bookings;
    }

    /**
     * checks if there exists a given gym belonging to given owner.
     * @param ownerId The ID of the gym owner.
     * @param gymId The ID of the gym.
     */
    public boolean validateGymId(int ownerId, int gymId) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_GYM_BY_OWNER_ID_AND_GYM_ID)) {
            ps.setInt(1, ownerId);
            ps.setInt(2, gymId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return true;
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Failed to validate gym ID for owner ID: " + ownerId, e);
        }
        return false;
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
     * Retrieves a gym centre from the database by their unique ID.
     *
     * @param gymId The ID of the gym to retrieve.
     * @return An Optional containing the GymCentre object if found, otherwise an empty Optional.
     * @throws DAOException if a database access error occurs.
     */
    public Optional<GymCentre> getGymById(int gymId) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_GYM_BY_GYM_ID)) {
            ps.setInt(1, gymId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToGymCentre(rs));
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Failed to retrieve gym by ID: " + gymId, e);
        }
        return Optional.empty();
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
                    rs.getInt("cost"),
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
     * Maps a ResultSet row to a Booking object.
     * @param rs The ResultSet containing the booking data.
     * @return A Booking object.
     * @throws DAOException If a database access error occurs during mapping.
     * @throws MissingValueException If a required value like bookingDate or bookingTime is null.
     */
    private Booking mapResultSetToBooking(ResultSet rs) throws SQLException {
        try {
            LocalDate bookingDate = null;
            if (rs.getDate("bookingDate") != null) {
                bookingDate = rs.getDate("bookingDate").toLocalDate();
            } else {
                throw new MissingValueException("Booking date is missing from database record.");
            }

            LocalDateTime dateAndTimeOfBooking = null;
            if (rs.getTime("dateAndTimeOfBooking") != null) {
                dateAndTimeOfBooking = rs.getTimestamp("dateAndTimeOfBooking").toLocalDateTime();
            } else {
                throw new MissingValueException("Date and time of booking is missing from database record.");
            }

            return new Booking(
                    rs.getInt("bookingId"),
                    rs.getInt("customerId"),
                    rs.getInt("gymId"),
                    rs.getInt("slotId"),
                    rs.getString("bookingStatus"),
                    bookingDate,
                    dateAndTimeOfBooking
            );
        } catch (SQLException e) {
            throw new DAOException("Failed to map ResultSet to Booking object.", e);
        }
    }
}