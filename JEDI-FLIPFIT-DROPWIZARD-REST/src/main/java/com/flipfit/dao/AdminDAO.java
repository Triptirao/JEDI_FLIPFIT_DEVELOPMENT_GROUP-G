package com.flipfit.dao;

import java.util.Optional;
import com.flipfit.bean.GymCentre;
import com.flipfit.bean.User;
import com.flipfit.utils.DBConnection;
import com.flipfit.exception.DAOException;
import com.flipfit.exception.UnableToDeleteUserException;
import com.flipfit.bean.Customer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Data Access Object (DAO) class for managing Admin-level operations in the FlipFit application.
 * This class handles database interactions related to administrative tasks such as
 * approving gyms, deleting users, and viewing pending requests.
 *
 * @author YourName
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
    private static final String DELETE_SLOTS_BY_GYM_ID = "DELETE FROM Slot WHERE gymId = ?";
    private static final String DELETE_BOOKINGS_BY_GYM_ID = "DELETE FROM Booking WHERE gymId = ?";
    private static final String SELECT_FUTURE_BOOKINGS_BY_CUSTOMER = "SELECT b.slotId, COUNT(b.bookingId) AS bookingCount FROM Booking AS b JOIN Slot AS s ON b.slotId = s.slotId WHERE b.customerId = ? AND CONCAT(b.bookingDate, ' ', s.startTime) > NOW() AND b.bookingStatus = 'BOOKED' GROUP BY b.slotId";
    private static final String UPDATE_BOOKED_COUNT = "UPDATE Slot SET bookedCount = bookedCount - ? WHERE slotId = ?";
    private static final String SELECT_BALANCE_AMOUNT = "SELECT SUM(gc.cost) AS total_balance FROM Booking AS b JOIN Slot AS s ON b.slotId = s.slotId JOIN GymCentre AS gc ON s.gymId = gc.centreId WHERE b.customerId = ? AND CONCAT(b.bookingDate, ' ', s.startTime) > NOW() AND b.bookingStatus = 'BOOKED'";
    private static final String UPDATE_CUSTOMER_BALANCE = "UPDATE Customer SET balance = balance + ? WHERE customerId = ?";
    private static final String SELECT_GYMS_BY_OWNER_ID = "SELECT * FROM GymCentre WHERE ownerId = ?";
    private static final String SELECT_REFUND_BALANCE = "SELECT b.customerId AS customerId, COUNT(b.bookingId) AS total_future_bookings, gc.cost AS cost FROM Booking AS b JOIN Slot AS s ON b.slotId = s.slotId JOIN GymCentre AS gc ON b.gymId = gc.centreId WHERE b.gymId = ? AND b.bookingStatus = 'BOOKED' AND CONCAT(b.bookingDate, ' ', s.startTime) > NOW() GROUP BY b.customerId";

    /**
     * Constructs an AdminDAO with necessary DAO dependencies.
     *
     * @param userDao     The DAO for User operations.
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
     *
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
     *
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
     *
     * @return A list of User objects representing pending gym owners.
     */
    public List<User> getPendingGymOwnerRequests() {
        return gymOwnerDao.getPendingGymOwners();
    }

    /**
     * Approves a gym owner's registration.
     *
     * @param email The email of the gym owner to be approved.
     * @throws DAOException If a database access error occurs or the email is not found.
     */
    public void approveGymOwnerRequest(String email) {
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
     *
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
     *
     * @return A list of User objects representing gym owners.
     */
    public List<User> getAllGymOwners() {
        return userDao.getAllGymOwners();
    }

    /**
     * Retrieves a list of all registered users.
     * This method delegates the call to the UserDAO.
     *
     * @return A list of all User objects.
     */
    public List<User> getAllUsers() {
        return userDao.getAllUsers();
    }

    /**
     * Retrieves a list of all registered customers.
     * This method delegates the call to the UserDAO.
     *
     * @return A list of all User objects representing customers.
     */
    public List<User> getAllCustomers() {
        return userDao.getAllCustomers();
    }

    /**
     * Deletes a user and all their associated data in a single transaction.
     * This method is designed to handle the deletion of a user regardless of their role
     * by attempting to delete from all possible role-specific tables.
     *
     * @param userId The unique identifier of the user to be deleted.
     * @throws UnableToDeleteUserException If the user cannot be deleted, or if the transaction fails.
     */
    public void deleteUser(int userId) throws UnableToDeleteUserException {
        Connection con = null;
        try {
            con = DBConnection.getConnection();
            con.setAutoCommit(false); // Start transaction

            // Check if the user is a customer and delete their data
            Optional<Customer> optionalCustomer = customerDao.getCustomerById(userId);
            if (optionalCustomer.isPresent()) {
                // Logic for deleting a customer
                Map<Integer, Integer> futureBookings = new HashMap<>();
                try (PreparedStatement ps = con.prepareStatement(SELECT_FUTURE_BOOKINGS_BY_CUSTOMER)) {
                    ps.setInt(1, userId);
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            futureBookings.put(rs.getInt("slotId"), rs.getInt("bookingCount"));
                        }
                    }
                }

                try (PreparedStatement ps = con.prepareStatement(UPDATE_BOOKED_COUNT)) {
                    for (Map.Entry<Integer, Integer> entry : futureBookings.entrySet()) {
                        ps.setInt(1, entry.getValue());
                        ps.setInt(2, entry.getKey());
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }

                int balanceAmount = 0;
                try (PreparedStatement ps = con.prepareStatement(SELECT_BALANCE_AMOUNT)) {
                    ps.setInt(1, userId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            balanceAmount = rs.getInt("total_balance");
                        }
                    }
                }
                try (PreparedStatement ps = con.prepareStatement(UPDATE_CUSTOMER_BALANCE)) {
                    ps.setInt(1, balanceAmount);
                    ps.setInt(2, userId);
                    ps.executeUpdate();
                }

                try (PreparedStatement ps = con.prepareStatement(DELETE_BOOKINGS_BY_USER_ID)) {
                    ps.setInt(1, userId);
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = con.prepareStatement(DELETE_CUSTOMER_BY_USER_ID)) {
                    ps.setInt(1, userId);
                    ps.executeUpdate();
                }
            }
            // Check if the user is a gym owner and delete their data
            else if (gymOwnerDao.getGymOwnerById(userId).isPresent()) {
                // Logic for deleting a gym owner
                List<Integer> gymIds = new ArrayList<>();
                try (PreparedStatement ps = con.prepareStatement(SELECT_GYMS_BY_OWNER_ID)) {
                    ps.setInt(1, userId);
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            gymIds.add(rs.getInt("centreId"));
                        }
                    }
                }

                for (Integer gymId : gymIds) {
                    try (PreparedStatement ps = con.prepareStatement(DELETE_BOOKINGS_BY_GYM_ID)) {
                        ps.setInt(1, gymId);
                        ps.executeUpdate();
                    }
                    try (PreparedStatement ps = con.prepareStatement(DELETE_SLOTS_BY_GYM_ID)) {
                        ps.setInt(1, gymId);
                        ps.executeUpdate();
                    }
                    try (PreparedStatement ps = con.prepareStatement(DELETE_GYM)) {
                        ps.setInt(1, gymId);
                        ps.executeUpdate();
                    }
                }
                try (PreparedStatement ps = con.prepareStatement(DELETE_GYM_OWNER_BY_USER_ID)) {
                    ps.setInt(1, userId);
                    ps.executeUpdate();
                }
            }
            // Check if the user is an admin
            else {
                // Logic for deleting an admin
                try (PreparedStatement ps = con.prepareStatement(DELETE_ADMIN_BY_USER_ID)) {
                    ps.setInt(1, userId);
                    ps.executeUpdate();
                }
            }

            // Finally, delete the user from the main User table.
            try (PreparedStatement ps = con.prepareStatement(DELETE_USER)) {
                ps.setInt(1, userId);
                int rowsAffected = ps.executeUpdate();
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
                // Log rollback failure
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
     * Deletes a gym center and all its associated data in a single transaction.
     * This method ensures data integrity by deleting dependent records first.
     *
     * @param gymId The unique identifier of the gym to be deleted.
     * @throws DAOException If the transaction fails or a database access error occurs.
     */
    public void deleteGymById(int gymId) {
        Connection con = null;
        try {
            con = DBConnection.getConnection();
            con.setAutoCommit(false);

            Map<Integer, Integer> refundBalance = new HashMap<>();

            try (PreparedStatement ps = con.prepareStatement(SELECT_REFUND_BALANCE)) {
                ps.setInt(1, gymId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        int customerId = rs.getInt("customerId");
                        int bookingCount = rs.getInt("total_future_bookings");
                        int cost = rs.getInt("cost");
                        refundBalance.put(customerId, bookingCount * cost);
                    }
                }
            }

            try (PreparedStatement ps = con.prepareStatement(UPDATE_CUSTOMER_BALANCE)) {
                for (Map.Entry<Integer, Integer> entry : refundBalance.entrySet()) {
                    ps.setInt(1, entry.getValue());
                    ps.setInt(2, entry.getKey());
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            try (PreparedStatement ps = con.prepareStatement(DELETE_BOOKINGS_BY_GYM_ID)) {
                ps.setInt(1, gymId);
                ps.executeUpdate();
            }

            try (PreparedStatement ps = con.prepareStatement(DELETE_SLOTS_BY_GYM_ID)) {
                ps.setInt(1, gymId);
                ps.executeUpdate();
            }

            try (PreparedStatement ps = con.prepareStatement(DELETE_GYM)) {
                ps.setInt(1, gymId);
                int rowsAffected = ps.executeUpdate();
                if (rowsAffected == 0) {
                    throw new DAOException("Could not delete gym. Gym with ID " + gymId + " not found.");
                }
            }

            con.commit();
        } catch (SQLException e) {
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (SQLException ex) {
                // Log rollback failure
            }
            throw new DAOException("Transaction failed: Gym deletion was rolled back.", e);
        } finally {
            if (con != null) {
                try {
                    con.setAutoCommit(true);
                    con.close();
                } catch (SQLException closeEx) {
                    // Log close failure
                }
            }
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
}
