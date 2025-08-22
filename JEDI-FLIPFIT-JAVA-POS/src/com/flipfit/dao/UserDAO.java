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

public class UserDAO {

    private static final String SELECT_ALL_CUSTOMERS = "SELECT u.* FROM User u JOIN Customer c ON u.userId = c.customerId";
    private static final String INSERT_USER = "INSERT INTO `User` (fullName, email, password, userPhone, city, pinCode) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String SELECT_ALL_USERS = "SELECT * FROM `User`";
    private static final String SELECT_USER_BY_ID = "SELECT * FROM `User` WHERE userId = ?";
    private static final String SELECT_USER_BY_EMAIL_PASSWORD = "SELECT * FROM `User` WHERE email = ? AND password = ?";
    private static final String DELETE_USER = "DELETE FROM `User` WHERE userId = ?";
    private static final String UPDATE_USER = "UPDATE `User` SET fullName = ?, email = ?, ?, userPhone = ?, city = ?, pinCode = ? WHERE userId = ?";
    private static final String SELECT_ALL_GYM_OWNERS = "SELECT u.* FROM User u JOIN GymOwner go ON u.userId = go.ownerId WHERE go.isApproved = TRUE";


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


    public List<User> getAllUsers() {
        List<User> userList = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_ALL_USERS);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                userList.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            throw new DAOException("Failed to retrieve all users.", e);
        }
        return userList;
    }

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

    public void deleteUser(int userId) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(DELETE_USER)) {
            ps.setInt(1, userId);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) {
                throw new DAOException("Could not delete user. User with ID " + userId + " not found.");
            }
        } catch (SQLException e) {
            throw new DAOException("Failed to delete user with ID: " + userId, e);
        }
    }

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
