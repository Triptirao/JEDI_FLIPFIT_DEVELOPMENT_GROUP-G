package com.flipfit.dao;

import com.flipfit.bean.Booking;
import com.flipfit.bean.Customer;
import com.flipfit.bean.GymCentre;
import com.flipfit.utils.DBConnection;
import com.flipfit.exception.DAOException;
import com.flipfit.exception.MissingValueException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object (DAO) class for managing customer-related operations in the FlipFit application.
 * This class handles database interactions for adding, retrieving, and updating customer details,
 * as well as managing bookings and viewing gyms.
 *
 * @author YourName
 */
public class CustomerDAO {

    // SQL queries for the customers table
    private static final String INSERT_CUSTOMER_DETAILS = "INSERT INTO Customer (customerId, paymentType, paymentInfo) VALUES (?, ?, ?)";
    private static final String SELECT_CUSTOMER_BY_ID = "SELECT * FROM Customer WHERE customerId = ?";
    private static final String UPDATE_CUSTOMER_DETAILS = "UPDATE Customer SET paymentType = ?, paymentInfo = ? WHERE customerId = ?";

    // SQL queries for booking table
    private static final String INSERT_BOOKING = "INSERT INTO Booking (customerId, gymId, slotId, bookingStatus, bookingDate, bookingTime) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String SELECT_BOOKINGS_BY_CUSTOMER_ID = "SELECT * FROM Booking WHERE customerId = ?";

    private static final String SELECT_APPROVED_GYMS = "SELECT * FROM GymCentre WHERE approved = TRUE";

    /**
     * Adds a new customer's payment details to the database.
     * @param customer The Customer object containing the details.
     * @throws DAOException If a database access error occurs.
     */
    public void addCustomer(Customer customer) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(INSERT_CUSTOMER_DETAILS)) {
            ps.setInt(1, customer.getUserId());
            ps.setInt(2, customer.getPaymentType());
            ps.setString(3, customer.getPaymentInfo());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Failed to add customer details.", e);
        }
    }

    /**
     * Retrieves a customer by their unique user ID.
     * @param userId The ID of the customer to retrieve.
     * @return An Optional containing the Customer object if found, otherwise an empty Optional.
     * @throws DAOException If a database access error occurs.
     */
    public Optional<Customer> getCustomerById(int userId) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_CUSTOMER_BY_ID)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToCustomer(rs));
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Failed to retrieve customer by ID: " + userId, e);
        }
        return Optional.empty();
    }

    /**
     * Updates an existing customer's payment details.
     * @param customer The Customer object with updated details.
     * @throws DAOException If a database access error occurs, or the customer ID is not found.
     */
    public void updateCustomer(Customer customer) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(UPDATE_CUSTOMER_DETAILS)) {
            ps.setInt(1, customer.getPaymentType());
            ps.setString(2, customer.getPaymentInfo());
            ps.setInt(3, customer.getUserId());
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) {
                throw new DAOException("Could not update customer. Customer with ID " + customer.getUserId() + " not found.");
            }
        } catch (SQLException e) {
            throw new DAOException("Failed to update customer details for ID: " + customer.getUserId(), e);
        }
    }

    /**
     * Books a slot for a customer by inserting a new record into the Booking table.
     * @param booking The Booking object containing all details.
     * @return The auto-generated booking ID.
     * @throws DAOException If a database access error occurs.
     */
    public int bookSlot(Booking booking) {
        int bookingId = -1;
        String sql = "INSERT INTO Booking (customerId, gymId, slotId, bookingStatus, bookingDate, bookingTime) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, booking.getCustomerId());
            ps.setInt(2, booking.getGymId());
            ps.setInt(3, booking.getSlotId());
            ps.setString(4, booking.getBookingStatus());
            ps.setObject(5, booking.getBookingDate());
            ps.setObject(6, booking.getBookingTime());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    bookingId = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Failed to book slot.", e);
        }

        return bookingId;
    }

    /**
     * Retrieves a list of all bookings for a given customer ID.
     * @param customerId The ID of the customer.
     * @return A list of Booking objects.
     * @throws DAOException If a database access error occurs.
     */
    public List<Booking> getBookingsByCustomerId(int customerId) {
        List<Booking> bookings = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_BOOKINGS_BY_CUSTOMER_ID)) {
            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    bookings.add(mapResultSetToBooking(rs));
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Failed to retrieve bookings for customer ID: " + customerId, e);
        }
        return bookings;
    }

    /**
     * Retrieves a list of all approved gym centers.
     * @return A list of GymCentre objects.
     * @throws DAOException If a database access error occurs.
     */
    public List<GymCentre> getApprovedGyms() {
        List<GymCentre> approvedGyms = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_APPROVED_GYMS);
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
     * Maps a ResultSet row to a Customer object.
     * @param rs The ResultSet containing the customer data.
     * @return A Customer object.
     * @throws DAOException If a database access error occurs during mapping.
     */
    private Customer mapResultSetToCustomer(ResultSet rs) throws SQLException {
        try {
            return new Customer(
                    rs.getInt("customerId"),
                    rs.getInt("paymentType"),
                    rs.getString("paymentInfo")
            );
        } catch (SQLException e) {
            throw new DAOException("Failed to map ResultSet to Customer object.", e);
        }
    }

    /**
     * Maps a ResultSet row to a GymCentre object.
     * @param rs The ResultSet containing the gym center data.
     * @return A GymCentre object.
     * @throws DAOException If a database access error occurs during mapping.
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

            LocalTime bookingTime = null;
            if (rs.getTime("bookingTime") != null) {
                bookingTime = rs.getTime("bookingTime").toLocalTime();
            } else {
                throw new MissingValueException("Booking time is missing from database record.");
            }

            return new Booking(
                    rs.getInt("bookingId"),
                    rs.getInt("customerId"),
                    rs.getInt("gymId"),
                    rs.getInt("slotId"),
                    rs.getString("bookingStatus"),
                    bookingDate,
                    bookingTime
            );
        } catch (SQLException e) {
            throw new DAOException("Failed to map ResultSet to Booking object.", e);
        }
    }
}
