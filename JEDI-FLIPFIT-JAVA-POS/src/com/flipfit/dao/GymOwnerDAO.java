package com.flipfit.dao;

import java.util.ArrayList;
import java.util.List;

public class GymOwnerDAO {

    // Hardcoded lists to simulate a database using String arrays
    private static List<String[]> gyms = new ArrayList<>();
    private static List<String[]> gymOwners = new ArrayList<>();

    static {
        // Dummy data for Gym Centres: {ID, OwnerID, Name, Capacity, IsApproved, City, State}
        gyms.add(new String[]{"1", "101", "Fit Hub", "50", "true", "Bengaluru", "Karnataka"});
        gyms.add(new String[]{"2", "101", "Powerhouse", "75", "true", "Bengaluru", "Karnataka"});

        // Dummy data for Gym Owners: {Role, ID, Name, Email, Password, Phone, City, Pincode, PAN, Aadhaar, GST, Approved}
        gymOwners.add(new String[]{"OWNER", "1", "Ravi Sharma", "ravi.sharma@example.com", "secure123", "9876543210", "Bengaluru", "560001", "ABCDE1234F", "123456789012", "29ABCDE1234F1Z5", "true"});
    }

    public void addGym(String[] gym) {
        gyms.add(gym);
    }

    public List<String[]> getGymsByOwnerId(String ownerId) {
        // For simplicity, returning all gyms
        return gyms;
    }

    public List<String[]> getAllCustomers() {
        // This would be implemented in a real application
        return new ArrayList<>();
    }

    public List<String[]> getAllPayments() {
        // This would be implemented in a real application
        return new ArrayList<>();
    }

    /**
     * Updates the details of a gym owner in the hardcoded list.
     * @param ownerId The ID of the owner to update.
     * @param choice The detail to change (1=Name, 2=City, 3=State).
     * @param newValue The new value for the detail.
     */
    public void updateGymOwnerDetails(String ownerId, int choice, String newValue) {
        for (String[] owner : gymOwners) {
            if (owner[1].equals(ownerId)) { // owner[1] is the ID
                switch (choice) {
                    case 1: // Change Name
                        owner[2] = newValue; // owner[2] is the Name
                        break;
                    case 2: // Change City
                        owner[6] = newValue; // owner[6] is the City
                        break;
                    case 3: // Change State
                        owner[7] = newValue; // owner[7] is the State
                        break;
                }
                return;
            }
        }
    }
}
