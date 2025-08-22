package com.flipfit.dao;

import com.flipfit.bean.Booking;
import com.flipfit.bean.Customer;
import com.flipfit.bean.GymCentre;
import com.flipfit.utils.DBConnection;
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

public class CustomerDAO {

    // SQL queries for the customers table
    private static final String INSERT_CUSTOMER_DETAILS = "INSERT INTO Customer (customerId, paymentType, paymentInfo) VALUES (?, ?, ?)";
    private static final String SELECT_CUSTOMER_BY_ID = "SELECT * FROM Customer WHERE customerId = ?";
    private static final String UPDATE_CUSTOMER_DETAILS = "UPDATE Customer SET paymentType = ?, paymentInfo = ? WHERE customerId = ?";

    // SQL queries for booking table
    private static final String INSERT_BOOKING = "INSERT INTO Booking (customerId, gymId, slotId, bookingStatus, bookingDate, bookingTime) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String SELECT_BOOKINGS_BY_CUSTOMER_ID = "SELECT * FROM Booking WHERE customerId = ?";

    private static final String SELECT_APPROVED_GYMS = "SELECT * FROM GymCentre WHERE approved = TRUE";

    public void addCustomer(Customer customer) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(INSERT_CUSTOMER_DETAILS)) {
            ps.setInt(1, customer.getUserId());
            ps.setInt(2, customer.getPaymentType());
            ps.setString(3, customer.getPaymentInfo());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

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
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public void updateCustomer(Customer customer) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(UPDATE_CUSTOMER_DETAILS)) {
            ps.setInt(1, customer.getPaymentType());
            ps.setString(2, customer.getPaymentInfo());
            ps.setInt(3, customer.getUserId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Books a slot for a customer by inserting a new record into the Booking table.
     * @param booking The Booking object containing all details.
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

            // Retrieve the auto-generated bookingId
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    bookingId = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return bookingId;
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
            e.printStackTrace();
        }
        return bookings;
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
            e.printStackTrace();
        }
        return approvedGyms;
    }

    private Customer mapResultSetToCustomer(ResultSet rs) throws SQLException {
        return new Customer(
                rs.getInt("customerId"),
                rs.getInt("paymentType"),
                rs.getString("paymentInfo")
        );
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

    private Booking mapResultSetToBooking(ResultSet rs) throws SQLException {
        return new Booking(
                rs.getInt("bookingId"),
                rs.getInt("customerId"),
                rs.getInt("gymId"),
                rs.getInt("slotId"),
                rs.getString("bookingStatus"),
                rs.getDate("bookingDate").toLocalDate(),
                rs.getTime("bookingTime").toLocalTime()
        );
    }
}
