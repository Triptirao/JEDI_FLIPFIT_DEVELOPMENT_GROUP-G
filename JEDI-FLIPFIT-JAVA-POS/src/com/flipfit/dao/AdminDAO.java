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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private static final String SELECT_ALL_GYMS = "SELECT * FROM GymCentre WHERE approved = TRUE";
    private static final String APPROVE_GYM = "UPDATE GymCentre SET approved = TRUE WHERE centreId = ?";
    private static final String DELETE_GYM = "DELETE FROM GymCentre WHERE centreId = ?";
    private static final String APPROVE_GYM_OWNER = "UPDATE `GymOwner` SET isApproved = TRUE WHERE ownerId = (SELECT userId FROM `User` WHERE email = ?)";
    private static final String DELETE_BOOKINGS_BY_USER_ID = "DELETE FROM Booking WHERE customerId = ?";
    private static final String DELETE_CUSTOMER_BY_USER_ID = "DELETE FROM Customer WHERE customerId = ?";
    private static final String DELETE_GYM_OWNER_BY_USER_ID = "DELETE FROM GymOwner WHERE ownerId = ?";
    private static final String DELETE_USER = "DELETE FROM User WHERE userId = ?";
    private static final String SELECT_FUTURE_BOOKINGS = "SELECT b.slotId AS slotId, COUNT(b.bookingId) AS total_future_bookings FROM Booking AS b JOIN Slot AS s ON b.slotId = s.slotId WHERE b.customerId = ? AND CONCAT(b.bookingDate, ' ', s.startTime) > NOW() AND b.bookingStatus = 'BOOKED' GROUP BY b.slotId";
    private static final String UPDATE_BOOKED_COUNT = "UPDATE Slot SET bookedCount = bookedCount - ? WHERE slotId = ?";
    private static final String SELECT_GYMS_BY_OWNER_ID = "SELECT * FROM GymCentre WHERE ownerId = ?";
    private static final String SELECT_BALANCE_AMOUNT = "SELECT SUM(gc.cost) AS balance_amount FROM Booking AS b JOIN Slot AS s ON b.slotId = s.slotId JOIN GymCentre AS gc ON s.gymId = gc.centreId WHERE b.customerId = ? AND CONCAT(b.bookingDate, ' ', s.startTime) > NOW() AND b.bookingStatus = 'BOOKED'";
    private static final String UPDATE_CUSTOMER_BALANCE = "UPDATE Customer SET balance = balance + ? WHERE customerId = ?";
    private static final String DELETE_BOOKINGS_BY_GYM_ID = "DELETE FROM Booking WHERE gymId = ?";
    private static final String DELETE_SLOTS_BY_GYM_ID = "DELETE FROM Slot WHERE gymId = ?";
    private static final String SELECT_REFUND_BALANCE = "SELECT b.customerId AS customerId, COUNT(b.bookingId) AS total_future_bookings, gc.cost AS cost FROM Booking AS b JOIN Slot AS s ON b.slotId = s.slotId JOIN GymCentre AS gc ON b.gymId = gc.centreId WHERE b.gymId = ? AND b.bookingStatus = 'BOOKED' AND CONCAT(b.bookingDate, ' ', s.startTime) > NOW() GROUP BY b.customerId";
    private static final String SELECT_PENDING_GYM_OWNERS = "SELECT u.* FROM User u JOIN GymOwner go ON u.userId = go.ownerId WHERE go.isApproved = FALSE";

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
     * Retrieves a list of gym owners who are awaiting administrative approval.
     *
     * @return A {@link List} of {@code User} objects representing the pending gym owners.
     * @throws DAOException if a database access error occurs.
     */
    public List<User> getPendingGymOwnerRequests() {
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
     * Retrieves a list of all registered customers.
     * This method delegates the call to the UserDAO.
     * @return A list of all User objects representing customers.
     */
    public List<User> getAllCustomers() {
        return userDao.getAllCustomers();
    }

    /**
     * Deletes a customer and all their associated data in a single transaction.
     * This method ensures data integrity by deleting dependent records first.
     * @param customerId The unique identifier of the customer to be deleted.
     * @throws DAOException If the connection cannot be closed.
     */
    public void deleteCustomer(int customerId) {
        Connection con = null;

        try{
            con = DBConnection.getConnection();
            con.setAutoCommit(false);

            Map<Integer, Integer> futureBookings = new HashMap<>();

            try (PreparedStatement ps = con.prepareStatement(SELECT_FUTURE_BOOKINGS)) {
                ps.setInt(1, customerId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        int slotId = rs.getInt("slotId");
                        int bookingCount = rs.getInt("total_future_bookings");
                        futureBookings.put(slotId, bookingCount);
                    }
                }
            }

            try (PreparedStatement ps = con.prepareStatement(UPDATE_BOOKED_COUNT)) {
                for (Map.Entry<Integer, Integer> entry : futureBookings.entrySet()) {
                    ps.setInt(1, entry.getValue()); // Decrement by the booking count
                    ps.setInt(2, entry.getKey());   // The slot ID to update
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            int balanceAmount = 0;

            try (PreparedStatement ps = con.prepareStatement(SELECT_BALANCE_AMOUNT)) {
                ps.setInt(1, customerId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        balanceAmount = rs.getInt("balance_amount");
                    }
                }
            }

            try  (PreparedStatement ps = con.prepareStatement(UPDATE_CUSTOMER_BALANCE)) {
                ps.setInt(1, balanceAmount);
                ps.setInt(2, customerId);
                ps.executeBatch();
            }

            try (PreparedStatement ps = con.prepareStatement(DELETE_BOOKINGS_BY_USER_ID)) {
                ps.setInt(1, customerId);
                ps.executeUpdate();
            }

            try (PreparedStatement ps = con.prepareStatement(DELETE_CUSTOMER_BY_USER_ID)) {
                ps.setInt(1, customerId);
                ps.executeUpdate();
            }

            try (PreparedStatement ps = con.prepareStatement(DELETE_USER)) {
                ps.setInt(1, customerId);
                ps.executeUpdate();
            }

            con.commit();
            System.out.println("User with ID: " + customerId + " deleted successfully.");
        } catch (SQLException e) {
            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException rollbackEx) {
                    System.out.println("Failed to rollback transaction: " + rollbackEx.getMessage());
                }
            }
            throw new DAOException("Transaction failed: Customer deletion was rolled back.", e);
        } finally {
            // Ensure the connection is closed and auto-commit is reset
            if (con != null) {
                try {
                    con.setAutoCommit(true);
                    con.close();
                } catch (SQLException closeEx) {
                    System.out.println("Failed to close connection: " + closeEx.getMessage());
                }
            }
        }
    }

    /**
     * Deletes a gym owner and all their associated data in a single transaction.
     * This method ensures data integrity by deleting dependent records first.
     * @param ownerId The unique identifier of the owner to be deleted.
     * @throws DAOException If the connection cannot be closed.
     */
    public void deleteOwner(int ownerId) {
        Connection con = null;

        try{
            con = DBConnection.getConnection();
            con.setAutoCommit(false);

            List<Integer> gymIds = new ArrayList<>();
            try (PreparedStatement ps = con.prepareStatement(SELECT_GYMS_BY_OWNER_ID)) {
                ps.setInt(1, ownerId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        gymIds.add(rs.getInt("centreId"));
                    }
                }
            }

            if (!gymIds.isEmpty()) {
                for (Integer gymId : gymIds) {
                    // Call the deleteGym method for each gym ID.
                    deleteGymById(con, gymId);
                }
            }

            try (PreparedStatement ps = con.prepareStatement(DELETE_GYM_OWNER_BY_USER_ID)) {
                ps.setInt(1, ownerId);
                ps.executeUpdate();
            }

            try (PreparedStatement ps = con.prepareStatement(DELETE_USER)) {
                ps.setInt(1, ownerId);
                ps.executeUpdate();
            }

            con.commit();
            System.out.println("User with ID: " + ownerId + " deleted successfully.");
        } catch (SQLException e) {
            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException rollbackEx) {
                    System.out.println("Failed to rollback transaction: " + rollbackEx.getMessage());
                }
            }
            throw new DAOException("Transaction failed: Gym Owner deletion was rolled back.", e);
        } finally {
            // Ensure the connection is closed and auto-commit is reset
            if (con != null) {
                try {
                    con.setAutoCommit(true);
                    con.close();
                } catch (SQLException closeEx) {
                    System.out.println("Failed to close connection: " + closeEx.getMessage());
                }
            }
        }
    }

    /**
     * Deletes a gym center from the database.
     * @param con The database connection object.
     * @param gymId The unique identifier of the gym to be deleted.
     * @throws DAOException If the gym cannot be deleted, or if the gymId does not exist.
     */
    public void deleteGymById(Connection con, int gymId) throws SQLException {

        Map<Integer, Integer> refundBalance = new HashMap<>();

        try (PreparedStatement ps = con.prepareStatement(SELECT_REFUND_BALANCE)) {
            ps.setInt(1, gymId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int customerId = rs.getInt("customerId");
                    int bookingCount = rs.getInt("total_future_bookings");
                    int cost = rs.getInt("cost");
                    refundBalance.put(customerId, bookingCount*cost);
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
            ps.executeUpdate();
        }
    }

    /**
     * Deletes a gym center from the database.
     * @param gymId The unique identifier of the gym to be deleted.
     * @throws DAOException If the gym cannot be deleted, or if the gymId does not exist.
     */
    public void deleteGym(int gymId) {
        Connection con = null;

        try {
            con = DBConnection.getConnection();
            con.setAutoCommit(false);
            deleteGymById(con, gymId);

            con.commit();
            System.out.println("Gym with ID: " + gymId + " deleted successfully.");
        } catch (SQLException e) {
            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException rollbackEx) {
                    System.out.println("Failed to rollback transaction: " + rollbackEx.getMessage());
                }
            }
            throw new DAOException("Transaction failed: Gym deletion was rolled back.", e);
        } finally {
            // Ensure the connection is closed and auto-commit is reset
            if (con != null) {
                try {
                    con.setAutoCommit(true);
                    con.close();
                } catch (SQLException closeEx) {
                    System.out.println("Failed to close connection: " + closeEx.getMessage());
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
