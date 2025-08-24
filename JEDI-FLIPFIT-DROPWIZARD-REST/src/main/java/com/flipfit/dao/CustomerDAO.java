package com.flipfit.dao;

import com.flipfit.bean.Booking;
import com.flipfit.bean.Customer;
import com.flipfit.bean.GymCentre;
import com.flipfit.bean.Slot;
import com.flipfit.utils.DBConnection;
import com.flipfit.exception.DAOException;
import com.flipfit.exception.MissingValueException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.AbstractMap;

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
    private static final String INSERT_BOOKING = "INSERT INTO Booking (customerId, gymId, slotId, bookingStatus, bookingDate, bookingTime) VALUES (?, ?, ?, ?, ?, ?)";
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
            ps.setInt(4, 0); // Default balance is 0
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
     * Books a slot for a customer by inserting a new record into the Booking table
     * and deducting the cost from the customer's balance. This is a transactional operation.
     *
     * @param booking The Booking object containing all details.
     * @param gymCost The cost of the slot.
     * @return The auto-generated booking ID.
     * @throws DAOException If a database access error occurs.
     */
    public Optional<Integer> bookSlot(Booking booking, int gymCost) {
        int bookingId = -1;
        // Corrected SQL query to use 'dateAndTimeOfBooking' to match the bean
        String insertBookingSql = "INSERT INTO Booking (customerId, gymId, slotId, bookingStatus, bookingDate, dateAndTimeOfBooking) VALUES (?, ?, ?, ?, ?, ?)";
        String updateBalanceSql = "UPDATE Customer SET balance = balance - ? WHERE customerId = ?";
        String updateSlotCountSql = "UPDATE Slot SET bookedCount = bookedCount + 1 WHERE slotId = ?";

        // Start the transaction
        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);

            // Step 1: Insert the new booking
            try (PreparedStatement insertPs = con.prepareStatement(insertBookingSql, Statement.RETURN_GENERATED_KEYS)) {
                insertPs.setInt(1, booking.getCustomerId());
                insertPs.setInt(2, booking.getGymId());
                insertPs.setInt(3, booking.getSlotId());
                insertPs.setString(4, booking.getBookingStatus());
                insertPs.setObject(5, booking.getBookingDate());
                insertPs.setObject(6, booking.getDateAndTimeOfBooking());
                insertPs.executeUpdate();

                try (ResultSet rs = insertPs.getGeneratedKeys()) {
                    if (rs.next()) {
                        bookingId = rs.getInt(1);
                    }
                }
            }

            // Step 2: Deduct the cost from the customer's balance
            try (PreparedStatement updatePs = con.prepareStatement(updateBalanceSql)) {
                updatePs.setInt(1, gymCost);
                updatePs.setInt(2, booking.getCustomerId());
                updatePs.executeUpdate();
            }

            // Step 3: Increment the booked count for the slot
            try (PreparedStatement updateSlotPs = con.prepareStatement(updateSlotCountSql)) {
                updateSlotPs.setInt(1, booking.getSlotId());
                updateSlotPs.executeUpdate();
            }

            con.commit(); // Commit the transaction if all steps succeed

        } catch (SQLException e) {
            // Rollback the transaction on failure
            try (Connection con = DBConnection.getConnection()) {
                if (con != null) {
                    con.rollback();
                }
            } catch (SQLException rollbackEx) {
                throw new DAOException("Transaction rollback failed.", rollbackEx);
            }
            throw new DAOException("Failed to book slot.", e);
        }

        return Optional.of(bookingId);
    }

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
            throw new DAOException("Failed to retrieve gym by ID: " + gymId, e);
        }
        return Optional.empty();
    }

    public Optional<Slot> getSlotById(int slotId) {
        // This method assumes you have a SlotDAO or similar logic to fetch a slot
        // For simplicity, this implementation is inlined here.
        String sql = "SELECT * FROM Slot WHERE slotId = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, slotId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Slot(
                            rs.getInt("slotId"),
                            rs.getInt("gymId"),
                            rs.getTime("startTime").toLocalTime(),
                            rs.getTime("endTime").toLocalTime(),
                            rs.getInt("capacity"),
                            rs.getInt("bookedCount")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Failed to retrieve slot by ID: " + slotId, e);
        }
        return Optional.empty();
    }

    public int getTotalBookingsBySlotIdAndBookingDate(int slotId, LocalDate bookingDate) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_NUMBER_OF_BOOKINGS)) {
            ps.setInt(1, slotId);
            ps.setObject(2, bookingDate);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Failed to retrieve total bookings for slot ID: " + slotId, e);
        }
        return 0;
    }

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
