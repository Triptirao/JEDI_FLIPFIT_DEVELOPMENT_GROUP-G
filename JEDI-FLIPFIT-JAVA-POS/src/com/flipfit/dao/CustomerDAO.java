package com.flipfit.dao;

import com.flipfit.bean.Booking;
import com.flipfit.bean.Customer;
import com.flipfit.bean.GymCentre;
import com.flipfit.bean.Slot;
import com.flipfit.utils.DBConnection;
import com.flipfit.exception.DAOException;
import com.flipfit.exception.MissingValueException;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private static final String INSERT_CUSTOMER_DETAILS = "INSERT INTO Customer (customerId, paymentType, paymentInfo, balance) VALUES (?, ?, ?, ?)";
    private static final String SELECT_CUSTOMER_BY_ID = "SELECT * FROM Customer WHERE customerId = ?";
    private static final String UPDATE_CUSTOMER_DETAILS = "UPDATE Customer SET paymentType = ?, paymentInfo = ? WHERE customerId = ?";
    private static final String SELECT_BALANCE_BY_ID = "SELECT balance FROM Customer WHERE customerId = ?";
    private static final String UPDATE_CUSTOMER_BALANCE = "UPDATE Customer SET balance = balance + ? WHERE customerId = ?";

    // SQL queries for booking table
    private static final String INSERT_BOOKING = "INSERT INTO Booking (customerId, gymId, slotId, bookingStatus, bookingDate, dateAndTimeOfBooking) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String SELECT_BOOKINGS_BY_CUSTOMER_ID = "SELECT * FROM Booking WHERE customerId = ?";
    private static final String SELECT_NUMBER_OF_BOOKINGS = "SELECT COUNT(*) FROM Booking WHERE slotId = ? AND bookingDate = ?";

    //SQL queries for gymCentre table
    private static final String SELECT_APPROVED_GYMS = "SELECT * FROM GymCentre WHERE approved = TRUE";
    private static final String SELECT_GYM_BY_ID = "SELECT * FROM GymCentre WHERE centreId = ? and approved = TRUE";

    //SQL queries for slot table
    private static final String SELECT_SLOT_BY_ID = "SELECT * FROM Slot WHERE slotId = ?";
    private static final String UPDATE_SLOT_BOOKING_COUNT = "UPDATE Slot SET bookedCount = bookedCount + 1 WHERE slotId = ?";

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
            ps.setInt(4, 0);
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
     * Retrieves a customer's wallet balance by unique customer ID.
     * @param customerId The ID of the customer to retrieve.
     * @return An Optional containing the customer's wallet balance if found, otherwise an empty Optional.
     * @throws DAOException If a database access error occurs.
     */
    public Optional<Integer> getBalanceById(int customerId) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_BALANCE_BY_ID)) {
            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int balance = rs.getInt("balance");
                    return Optional.of(balance);
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Failed to retrieve balance for customer ID: " + customerId, e);
        }
        return Optional.empty();
    }

    /**
     * Updates an existing customer's wallet balance.
     * @param customerId The ID of the customer to retrieve.
     * @param balance The balance to be added to wallet.
     * @throws DAOException If a database access error occurs.
     */
    public void makePayment(int customerId, int balance) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(UPDATE_CUSTOMER_BALANCE)) {
            ps.setInt(1, balance);
            ps.setInt(2, customerId);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) {
                throw new DAOException("Could not update customer's wallet balance. Customer with ID " + customerId + " not found.");
            }
        } catch (SQLException e) {
            throw new DAOException("Failed to update customer's wallet balance for ID: " + customerId, e);
        }
    }

    /**
     * Updates an existing customer's payment details.
     *
     * @param customer The Customer object with updated details.
     * @throws DAOException If a database access error occurs, or the customer ID is not found.
     */
    public void updateCustomerDetails(Customer customer) {
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
    public Optional<Integer> bookSlot(Booking booking, int gymCost) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(INSERT_BOOKING, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, booking.getCustomerId());
            ps.setInt(2, booking.getGymId());
            ps.setInt(3, booking.getSlotId());
            ps.setString(4, booking.getBookingStatus());
            ps.setObject(5, booking.getBookingDate());
            ps.setObject(6, booking.getDateAndTimeOfBooking());
            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        makePayment(booking.getCustomerId(), (-1*gymCost));
                        updateSlotBookingCount(booking.getSlotId());
                        return Optional.of(rs.getInt(1));
                    }
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Failed to book slot.", e);
        }

        return Optional.empty();
    }

    /**
     * Updates an existing slot's booking count.
     *
     * @param slotId The ID od slot to be updated.
     * @throws DAOException If a database access error occurs.
     */
    public void updateSlotBookingCount(int slotId) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(UPDATE_SLOT_BOOKING_COUNT)) {
            ps.setInt(1, slotId);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) {
                throw new DAOException("Could not update slot booking count. Slot with ID " + slotId + " not found.");
            }
        } catch (SQLException e) {
            throw new DAOException("Failed to update slot booking count for ID: " + slotId, e);
        }
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
     * Retrieves gym centre with given gym ID.
     * @return A GymCentre object.
     * @throws DAOException If a database access error occurs.
     */
    public Optional<GymCentre> getGymById(int gymId) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_GYM_BY_ID)) {
            ps.setInt(1, gymId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToGymCentre(rs));
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Failed to retrieve gym with ID: " + gymId, e);
        }
        return Optional.empty();
    }

    /**
     * Retrieves slot with given slot ID.
     * @return A Slot object.
     * @throws DAOException If a database access error occurs.
     */
    public Optional<Slot> getSlotById(int slotId) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_SLOT_BY_ID)) {
            ps.setInt(1, slotId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToSlot(rs));
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Failed to retrieve slot with ID: " + slotId, e);
        }
        return Optional.empty();
    }

    /**
     * Retrieves number of bookings for a given slot on a particular date.
     * @param slotId The ID of the slot.
     * @param bookingDate The date of booking.
     * @return An Optional containing the number of bookings if found, otherwise an empty Optional.
     * @throws DAOException If a database access error occurs.
     */
    public Optional<Integer> getTotalBookingsBySlotIdAndBookingDate(int slotId, LocalDate bookingDate) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_NUMBER_OF_BOOKINGS)) {
            ps.setInt(1, slotId);
            ps.setDate(2, Date.valueOf(bookingDate));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int bookings = rs.getInt(1);
                    return Optional.of(bookings);
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Failed to retrieve total bookings for ID: " + slotId, e);
        }
        return Optional.empty();
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
                    rs.getString("paymentInfo"),
                    rs.getInt("balance")
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

    /**
     * Maps a ResultSet row to a Slot object.
     * @param rs The ResultSet containing the slot data.
     * @return A Slot object.
     * @throws DAOException If a database access error occurs during mapping.
     */
    private Slot mapResultSetToSlot(ResultSet rs) throws SQLException {
        try {
            return new Slot(
                    rs.getInt("slotId"),
                    rs.getInt("gymId"),
                    rs.getTime("startTime").toLocalTime(),
                    rs.getTime("endTime").toLocalTime(),
                    rs.getInt("capacity"),
                    rs.getInt("bookedCount")
            );
        } catch (SQLException e) {
            throw new DAOException("Failed to map ResultSet to Slot object.", e);
        }
    }
}
