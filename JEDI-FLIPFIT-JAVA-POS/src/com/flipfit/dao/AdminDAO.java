package com.flipfit.dao;

import com.flipfit.utils.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AdminDAO {

    public List<String[]> getPendingGymRequests() {
        List<String[]> gyms = new ArrayList<>();
        String sql = "SELECT centreId, name FROM gymCentre WHERE approved = false";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                String[] gym = new String[2];
                gym[0] = String.valueOf(rs.getInt("centreId"));
                gym[1] = rs.getString("name");
                gyms.add(gym);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return gyms;
    }

    public void approveGymRequest(String gymId) {
        String sql = "UPDATE gymCentre SET approved = true WHERE centreId = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, gymId);
            pstmt.executeUpdate();
            System.out.println("Gym request for " + gymId + " approved successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<String[]> getPendingGymOwnerRequests() {
        List<String[]> owners = new ArrayList<>();
        String sql = "SELECT u.userId, u.fullName, u.email FROM user u INNER JOIN gymOwner go ON u.userId = go.userId INNER JOIN role r ON u.roleId = r.id LEFT JOIN gymCentre gc ON go.userId = gc.ownerId WHERE r.role = 'gymowner' AND gc.approved IS NULL OR gc.approved = false";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                String[] user = new String[3];
                user[0] = String.valueOf(rs.getInt("userId"));
                user[1] = rs.getString("fullName");
                user[2] = rs.getString("email");
                owners.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return owners;
    }

    public void approveGymOwnerRequest(String email) {
        String sqlSelect = "SELECT gc.centreId FROM gymCentre gc INNER JOIN gymOwner go ON gc.ownerId = go.userId INNER JOIN user u ON go.userId = u.userId WHERE u.email = ?";
        String sqlUpdate = "UPDATE gymCentre SET approved = true WHERE centreId = ?";

        try (Connection conn = DBConnection.getConnection()) {
            int centreIdToApprove = -1;

            try (PreparedStatement pstmtSelect = conn.prepareStatement(sqlSelect)) {
                pstmtSelect.setString(1, email);
                try (ResultSet rs = pstmtSelect.executeQuery()) {
                    if (rs.next()) {
                        centreIdToApprove = rs.getInt("centreId");
                    }
                }
            }

            if (centreIdToApprove != -1) {
                try (PreparedStatement pstmtUpdate = conn.prepareStatement(sqlUpdate)) {
                    pstmtUpdate.setInt(1, centreIdToApprove);
                    int rowsAffected = pstmtUpdate.executeUpdate();

                    if (rowsAffected > 0) {
                        System.out.println("Gym request for owner with email " + email + " approved successfully.");
                    } else {
                        System.out.println("No gym request found for owner with email " + email + ".");
                    }
                }
            } else {
                System.out.println("No pending gym request found for the provided owner email.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<String[]> getAllGyms() {
        List<String[]> gyms = new ArrayList<>();
        String sql = "SELECT centreId, name, city FROM gymCentre";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                String[] gym = new String[3];
                gym[0] = String.valueOf(rs.getInt("centreId"));
                gym[1] = rs.getString("name");
                gym[2] = rs.getString("city");
                gyms.add(gym);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return gyms;
    }

    public List<String[]> getAllGymOwners() {
        List<String[]> owners = new ArrayList<>();
        String sql = "SELECT u.userId, u.fullName, u.email FROM user u INNER JOIN gymOwner go ON u.userId = go.userId INNER JOIN gymCentre gc ON go.userId = gc.ownerId WHERE gc.approved = true";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                String[] user = new String[3];
                user[0] = String.valueOf(rs.getInt("userId"));
                user[1] = rs.getString("fullName");
                user[2] = rs.getString("email");
                owners.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return owners;
    }

    public List<String[]> getAllUsers() {
        List<String[]> users = new ArrayList<>();
        String sql = "SELECT u.userId, u.fullName, u.email, r.role FROM user u INNER JOIN role r ON u.roleId = r.id";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                String[] user = new String[4];
                user[0] = String.valueOf(rs.getInt("userId"));
                user[1] = rs.getString("fullName");
                user[2] = rs.getString("email");
                user[3] = rs.getString("role");
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    public List<String[]> getAllCustomers() {
        List<String[]> customers = new ArrayList<>();
        String sql = "SELECT u.userId, u.fullName, u.email, u.userPhone, u.city, u.pincode FROM user u INNER JOIN role r ON u.roleId = r.id WHERE r.role = 'customer'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                String[] user = new String[6];
                user[0] = String.valueOf(rs.getInt("userId"));
                user[1] = rs.getString("fullName");
                user[2] = rs.getString("email");
                user[3] = rs.getString("userPhone");
                user[4] = rs.getString("city");
                user[5] = String.valueOf(rs.getInt("pincode"));
                customers.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customers;
    }

    public void deleteUser(String userId) {
        String sql = "DELETE FROM user WHERE userId = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            pstmt.executeUpdate();
            System.out.println("User with ID " + userId + " deleted.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteGym(String gymId) {
        String sql = "DELETE FROM gymCentre WHERE centreId = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, gymId);
            pstmt.executeUpdate();
            System.out.println("Gym with ID " + gymId + " deleted.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}