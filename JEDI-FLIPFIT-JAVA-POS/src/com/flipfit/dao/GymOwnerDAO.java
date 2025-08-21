package com.flipfit.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class GymOwnerDAO {

    // This DAO now relies on other DAOs for core data.
    private UserDAO userDao = new UserDAO();

    // Data for Gym Owners
    private static List<String[]> gymOwners = new ArrayList<>();
    // Data for Gym Centres
    private static List<String[]> gymCentres = new ArrayList<>();

    static {
        // Hardcoded data for Gym Owners: {Role, ID, Name, Email, Password, Phone, City, Pincode, PAN, Aadhaar, GST, Approved}
        gymOwners.add(new String[]{"OWNER", "1", "Ravi Sharma", "ravi.sharma@example.com", "secure123", "9876543210", "Bengaluru", "560001", "ABCDE1234F", "123456789012", "29ABCDE1234F1Z5", "true"});

        // Hardcoded data for Gym Centres: {ID, OwnerID, Name, Slots, Capacity, Approved, City, State}
        gymCentres.add(new String[]{"1", "101", "Fitness Hub", "slot1,slot2,slot3", "50", "false", "Bengaluru", "Karnataka"});
        gymCentres.add(new String[]{"2", "102", "Zenith Fitness", "slot1,slot2,slot3", "75", "true", "Bengaluru", "Karnataka"});
        gymCentres.add(new String[]{"3", "103", "Flex Gym", "slot1,slot2", "45", "false", "Mumbai", "Maharashtra"});
    }

    public void addGym(String[] gym) {
        gymCentres.add(gym);
    }

    public List<String[]> getGymsByOwnerId(String ownerId) {
        return gymCentres.stream()
                .filter(gym -> gym[1].equals(ownerId))
                .collect(Collectors.toList());
    }

    public List<String[]> getAllCustomers() {
        return userDao.getAllUsers().stream()
                .filter(user -> user[0].equals("CUSTOMER"))
                .collect(Collectors.toList());
    }

    public List<String[]> getPaymentsByGym(String gymId) {
        // This would be implemented by a PaymentDAO in a real application
        return new ArrayList<>();
    }

    public static List<String[]> getAllGyms() {
        return gymCentres;
    }

    public List<String[]> getPendingGymRequests() {
        return gymCentres.stream()
                .filter(gym -> gym[5].equals("false"))
                .collect(Collectors.toList());
    }

    public void approveGymRequest(String gymId) {
        gymCentres.stream()
                .filter(gym -> gym[0].equals(gymId))
                .findFirst()
                .ifPresent(gym -> gym[5] = "true");
    }

    public void deleteGym(String gymId) {
        gymCentres.removeIf(gym -> gym[0].equals(gymId));
    }

    public List<String[]> getApprovedGyms() {
        return gymCentres.stream()
                .filter(gym -> gym[5].equals("true"))
                .collect(Collectors.toList());
    }

    /**
     * Updates the details of a gym owner in the hardcoded list.
     * @param ownerId The ID of the owner to update.
     * @param choice The detail to change (1=Name, 2=Email, 3=Password, 4=Phone, 5=City, 6=Pincode, 7=PAN, 8=Aadhaar, 9=GST).
     * @param newValue The new value for the detail.
     */
    public void updateGymOwnerDetails(String ownerId, int choice, String newValue) {
        userDao.getAllUsers().stream()
                .filter(user -> user[1].equals(ownerId))
                .findFirst()
                .ifPresent(owner -> {
                    switch (choice) {
                        case 1: owner[2] = newValue; break; // Name
                        case 2: owner[3] = newValue; break; // Email
                        case 3: owner[4] = newValue; break; // Password
                        case 4: owner[5] = newValue; break; // Phone
                        case 5: owner[6] = newValue; break; // City
                        case 6: owner[7] = newValue; break; // Pincode
                        case 7: owner[8] = newValue; break; // PAN (Assuming this is the correct index)
                        case 8: owner[9] = newValue; break; // Aadhaar (Assuming this is the correct index)
                        case 9: owner[10] = newValue; break; // GST (Assuming this is the correct index)
                    }
                });
    }
}