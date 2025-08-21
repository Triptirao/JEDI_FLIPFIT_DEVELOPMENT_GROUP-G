package com.flipfit.dao;
import com.flipfit.utils.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GymOwnerDAO {

    public void addGym(String[] gym) {
        String sql = "INSERT INTO `gymCentres` (`centreId`, `ownerId`, `name`, `slots`, `capacity`, `approved`, `city`, `state`) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, Integer.parseInt(gym[0])); // ID
            pstmt.setInt(2, Integer.parseInt(gym[1])); // OwnerID
            pstmt.setString(3, gym[2]); // Name
            pstmt.setString(4, gym[3]); // Slots
            pstmt.setInt(5, Integer.parseInt(gym[4])); // Capacity
            pstmt.setBoolean(6, Boolean.parseBoolean(gym[5])); // Approved
            pstmt.setString(7, gym[6]); // City
            pstmt.setString(8, gym[7]); // State
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<String[]> getGymsByOwnerId(String ownerId) {
        List<String[]> gyms = new ArrayList<>();
        String sql = "SELECT * FROM `gymCentres` WHERE `ownerId` = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, Integer.parseInt(ownerId));
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                gyms.add(new String[]{
                        String.valueOf(rs.getInt("centreId")),
                        String.valueOf(rs.getInt("ownerId")),
                        rs.getString("name"),
                        rs.getString("slots"),
                        String.valueOf(rs.getInt("capacity")),
                        String.valueOf(rs.getBoolean("approved")),
                        rs.getString("city"),
                        rs.getString("state")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return gyms;
    }

    // This method will now fetch from a hypothetical `customers` table
    public List<String[]> getAllCustomers() {
        // This would require a database connection and a query to a `users` table
        // filtering for the CUSTOMER role.
        return new ArrayList<>();
    }

    public List<String[]> getPaymentsByGym(String gymId) {
        return new ArrayList<>();
    }

    public static List<String[]> getAllGyms() {
        List<String[]> gyms = new ArrayList<>();
        String sql = "SELECT * FROM `gymCentres`";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                gyms.add(new String[]{
                        String.valueOf(rs.getInt("centreId")),
                        String.valueOf(rs.getInt("ownerId")),
                        rs.getString("name"),
                        rs.getString("slots"),
                        String.valueOf(rs.getInt("capacity")),
                        String.valueOf(rs.getBoolean("approved")),
                        rs.getString("city"),
                        rs.getString("state")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return gyms;
    }

    public List<String[]> getPendingGymRequests() {
        List<String[]> gyms = new ArrayList<>();
        String sql = "SELECT * FROM `gymCentres` WHERE `approved` = FALSE";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                gyms.add(new String[]{
                        String.valueOf(rs.getInt("centreId")),
                        String.valueOf(rs.getInt("ownerId")),
                        rs.getString("name"),
                        rs.getString("slots"),
                        String.valueOf(rs.getInt("capacity")),
                        String.valueOf(rs.getBoolean("approved")),
                        rs.getString("city"),
                        rs.getString("state")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return gyms;
    }

    public void approveGymRequest(String gymId) {
        String sql = "UPDATE `gymCentres` SET `approved` = TRUE WHERE `centreId` = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, Integer.parseInt(gymId));
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteGym(String gymId) {
        String sql = "DELETE FROM `gymCentres` WHERE `centreId` = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, Integer.parseInt(gymId));
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<String[]> getApprovedGyms() {
        List<String[]> gyms = new ArrayList<>();
        String sql = "SELECT * FROM `gymCentres` WHERE `approved` = TRUE";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                gyms.add(new String[]{
                        String.valueOf(rs.getInt("centreId")),
                        String.valueOf(rs.getInt("ownerId")),
                        rs.getString("name"),
                        rs.getString("slots"),
                        String.valueOf(rs.getInt("capacity")),
                        String.valueOf(rs.getBoolean("approved")),
                        rs.getString("city"),
                        rs.getString("state")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return gyms;
    }

    public void updateGymOwnerDetails(String ownerId, int choice, String newValue) {
        String columnName = getColumnNameForChoice(choice);
        if (columnName == null) {
            return; // Invalid choice
        }

        String sql = "UPDATE `gymOwners` SET `" + columnName + "` = ? WHERE `userId` = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newValue);
            pstmt.setInt(2, Integer.parseInt(ownerId));
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String getColumnNameForChoice(int choice) {
        switch (choice) {
            case 1: return "fullName";
            case 2: return "email";
            case 3: return "password";
            case 4: return "userPhone";
            case 5: return "city";
            case 6: return "pinCode";
            case 7: return "pan";
            case 8: return "aadhaar";
            case 9: return "gst";
            default: return null;
        }
    }
}