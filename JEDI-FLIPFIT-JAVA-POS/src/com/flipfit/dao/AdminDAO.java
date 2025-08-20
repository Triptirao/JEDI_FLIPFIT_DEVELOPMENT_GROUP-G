package com.flipfit.dao;

import java.util.ArrayList;
import java.util.List;

public class AdminDAO {

    private static List<String[]> gymCentres = new ArrayList<>();
    private static List<String[]> gymOwners = new ArrayList<>();
    private static List<String[]> users = new ArrayList<>();

    static {
        // Dummy data for Gym Centres: {ID, OwnerID, Name, Slots, Capacity, Approved, City, State, Pincode, Facilities}
        gymCentres.add(new String[]{"1", "101", "Fitness Hub", "slot1,slot2,slot3", "50", "false", "Bengaluru", "Karnataka", "600001", "Cardio,Weights"});
        gymCentres.add(new String[]{"2", "102", "Zenith Fitness", "slot1,slot2,slot3", "75", "true", "Bengaluru", "Karnataka", "560001", "Yoga,Pool"});
        gymCentres.add(new String[]{"3", "103", "Flex Gym", "slot1,slot2", "45", "false", "Mumbai", "Maharashtra", "400001", "Weights,Showers"});

        // Dummy data for Gym Owners: {Role, ID, Name, Email, Password, Phone, City, Pincode, PAN, Aadhaar, GST, Approved}
        gymOwners.add(new String[]{"OWNER", "1", "Ravi Sharma", "ravi.sharma@example.com", "secure123", "9876543210", "Bengaluru", "560001", "ABCDE1234F", "123456789012", "29ABCDE1234F1Z5", "false"});
        gymOwners.add(new String[]{"OWNER", "2", "Priya Singh", "priya.singh@example.com", "mysecretpass", "8765432109", "Mumbai", "400001", "FGHIJ5678K", "987654321098", "27FGHIJ5678K1Z3", "true"});

        // Dummy data for users: {Role, ID, Name, Email, Password, Phone, City, Pincode}
        users.add(new String[]{"CUSTOMER", "1", "John Doe", "john.doe@example.com", "pass123", "1234567890", "New York", "10001"});
        users.add(new String[]{"ADMIN", "1", "Admin User", "admin@flipfit.com", "admin123", "9876543210", "New Delhi", "110001"});
    }

    public List<String[]> getPendingGymRequests() {
        List<String[]> pending = new ArrayList<>();
        for (String[] gym : gymCentres) {
            if (gym[5].equals("false")) {
                pending.add(gym);
            }
        }
        return pending;
    }

    public void approveGymRequest(String gymId) {
        for (String[] gym : gymCentres) {
            if (gym[0].equals(gymId)) {
                gym[5] = "true";
                return;
            }
        }
    }

    public List<String[]> getPendingGymOwnerRequests() {
        List<String[]> pendingOwners = new ArrayList<>();
        for (String[] owner : gymOwners) {
            if (owner[11].equals("false")) {
                pendingOwners.add(owner);
            }
        }
        return pendingOwners;
    }

    public void approveGymOwnerRequest(String email) {
        for (String[] owner : gymOwners) {
            if (owner[3].equals(email)) {
                owner[11] = "true";
                return;
            }
        }
    }

    public List<String[]> getAllGyms() {
        return gymCentres;
    }

    public List<String[]> getAllGymOwners() {
        return gymOwners;
    }

    public void deleteUser(int userId) {
        users.removeIf(user -> user[1].equals(String.valueOf(userId)));
    }

    public void deleteGym(int gymId) {
        gymCentres.removeIf(gym -> gym[0].equals(String.valueOf(gymId)));
    }

    public List<String[]> getAllUsers() {
        return users;
    }
}
