package com.flipfit.dao;

import com.flipfit.bean.User;
import com.flipfit.exception.DAOException;
import com.flipfit.exception.DuplicateEntryException;
import com.flipfit.exception.MissingValueException;
import com.flipfit.utils.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * The UserDAO class provides data access operations for the User entity.
 * It handles all database interactions related to user management, including
 * adding, retrieving, updating, and deleting user records.
 */
public class UserDAO {

    private static final String SELECT_ALL_CUSTOMERS = "SELECT u.* FROM User u JOIN Customer c ON u.userId = c.customerId";
    private static final String INSERT_USER = "INSERT INTO `User` (fullName, email, password, userPhone, city, pinCode) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String SELECT_USER_BY_ID = "SELECT * FROM `User` WHERE userId = ?";
    private static final String SELECT_USER_BY_EMAIL_PASSWORD = "SELECT * FROM `User` WHERE email = ? AND password = ?";
    private static final String UPDATE_USER = "UPDATE `User` SET fullName = ?, email = ?, password = ?, userPhone = ?, city = ?, pinCode = ? WHERE userId = ?";
    private static final String SELECT_ALL_GYM_OWNERS = "SELECT u.* FROM User u JOIN GymOwner go ON u.userId = go.ownerId WHERE go.isApproved = TRUE";

    /**
     * Adds a new user record to the database.
     *
     * @param user The User object containing the data to be added.
     * @return The auto-generated user ID if the insertion is successful, otherwise -1.
     * @throws DuplicateEntryException if a user with the same unique email already exists.
     * @throws DAOException if a general database access error occurs.
     */
    public int addUser(User user) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(INSERT_USER, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getFullName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            ps.setLong(4, user.getUserPhone());
            ps.setString(5, user.getCity());
            ps.setInt(6, user.getPinCode());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            // Check for duplicate entry SQL state (a common state for unique constraint violations)
            if (e.getSQLState().equals("23000")) {
                throw new DuplicateEntryException("User with this email already exists.", e);
            }
            throw new DAOException("Failed to add new user.", e);
        }
        return -1;
    }

    /**
     * Retrieves a list of all users who are customers.
     *
     * @return A list of User objects representing all customers.
     * @throws DAOException if a database access error occurs.
     */
    public List<User> getAllCustomers() {
        List<User> customers = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_ALL_CUSTOMERS);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                customers.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            throw new DAOException("Failed to retrieve all customers.", e);
        }
        return customers;
    }

    /**
     * Retrieves a list of all users who are approved gym owners.
     *
     * @return A list of User objects representing all approved gym owners.
     * @throws DAOException if a database access error occurs.
     */
    public List<User> getAllGymOwners() {
        List<User> gymOwners = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_ALL_GYM_OWNERS);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                gymOwners.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            throw new DAOException("Failed to retrieve all gym owners.", e);
        }
        return gymOwners;
    }

    /**
     * Retrieves a user from the database by their unique ID.
     *
     * @param userId The ID of the user to retrieve.
     * @return An Optional containing the User object if found, otherwise an empty Optional.
     * @throws DAOException if a database access error occurs.
     */
    public Optional<User> getUserById(int userId) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_USER_BY_ID)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Failed to retrieve user by ID: " + userId, e);
        }
        return Optional.empty();
    }

    /**
     * Authenticates a user by checking their email and password against the database.
     *
     * @param email The user's email address.
     * @param password The user's password.
     * @return An Optional containing the User object if authentication is successful, otherwise an empty Optional.
     * @throws DAOException if a database access error occurs.
     */
    public Optional<User> getUserByEmailAndPassword(String email, String password) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_USER_BY_EMAIL_PASSWORD)) {
            ps.setString(1, email);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Failed to authenticate user.", e);
        }
        return Optional.empty();
    }

    /**
     * Updates an existing user record in the database.
     *
     * @param user The User object with the updated details.
     * @throws DuplicateEntryException if the updated email already exists for another user.
     * @throws DAOException if the user is not found or a general database access error occurs.
     */
    public void updateUser(User user) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(UPDATE_USER)) {
            ps.setString(1, user.getFullName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            ps.setLong(4, user.getUserPhone());
            ps.setString(5, user.getCity());
            ps.setInt(6, user.getPinCode());
            ps.setInt(7, user.getUserId());
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) {
                throw new DAOException("Could not update user. User with ID " + user.getUserId() + " not found.");
            }
        } catch (SQLException e) {
            if (e.getSQLState().equals("23000")) {
                throw new DuplicateEntryException("User with this email already exists.", e);
            }
            throw new DAOException("Failed to update user details for ID: " + user.getUserId(), e);
        }
    }

    /**
     * Maps a row from a ResultSet to a User object.
     *
     * @param rs The ResultSet containing user data.
     * @return A new User object populated with data from the ResultSet.
     * @throws SQLException if an error occurs while accessing the result set.
     * @throws MissingValueException if a required value (e.g., fullName, email) is missing from the database record.
     * @throws DAOException if a general error occurs during the mapping process.
     */
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        try {
            // Check for potential null values before returning the User object
            if (rs.getString("fullName") == null || rs.getString("email") == null) {
                throw new MissingValueException("Missing required data (fullName or email) from database record.");
            }

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