package com.flipfit.business;

import com.flipfit.bean.*;
import com.flipfit.dao.CustomerDAO;
import com.flipfit.dao.GymOwnerDAO;
import com.flipfit.dao.UserDAO;
import com.flipfit.exception.MismatchinputException;

import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * The GymOwnerService class provides the business logic for all operations
 * a gym owner can perform, such as managing gym centers, viewing customer lists,
 * and editing their personal details.
 *
 * @author
 */
public class GymOwnerService implements gymOwnerInterface {

    private static final Scanner in = new Scanner(System.in);
    private GymOwnerDAO gymOwnerDao;
    private UserDAO userDao;
    private CustomerDAO customerDao;

    public GymOwnerService(UserDAO userDao, CustomerDAO customerDao,GymOwnerDAO gymOwnerDao) {
        this.gymOwnerDao = gymOwnerDao;
        this.userDao = userDao;
        this.customerDao = customerDao;
    }

    /**
     * Adds a new gym center to the system.
     * @param gymData A GymCentre object containing the details of the new gym center.
     */
    @Override
    public void addCentre(GymCentre gymData) {
        int gymId = gymOwnerDao.addGym(gymData);
        System.out.println("Adding new gym centre: " + gymData.getCentreName());

        if (gymId != -1) {
            // Create slots and link it to the gym
            Slot newSlot = new Slot(gymId, gymData.getCapacity());
            gymOwnerDao.addSlots(newSlot);
        } else {
            throw new MismatchinputException("Gym registration failed due to an internal error.");
        }
    }

    /**
     * Retrieves and displays the details of all gym centers owned by a specific gym owner.
     * @param ownerId The unique ID of the gym owner.
     */
    @Override
    public void viewGymDetails(int ownerId) {
        System.out.println("Fetching details for all your gym centres...");
        List<GymCentre> gyms = null;
        gyms = gymOwnerDao.getGymsByOwnerId(ownerId);

        System.out.println("------------------------------------------------------------------");
        System.out.printf("%-15s %-20s %-20s %-20s %n", "Gym ID", "Name", "Location", "Approval Status");
        System.out.println("------------------------------------------------------------------");
        if (gyms.isEmpty()) {
            System.out.println("No gym centers found.");
        } else {
            for (GymCentre gym : gyms) {
                System.out.printf("%-15s %-20s %-20s %-20s %n", gym.getCentreId(), gym.getCentreName(), gym.getCity() + ", " + gym.getState(), gym.isApproved()? "Approved" : "Pending");
            }
        }
        System.out.println("------------------------------------------------------------------");
    }

    /**
     * Retrieves and displays the list of customers for the gym owner's centers.
     */
    @Override
    public void viewCustomers() {
        System.out.println("Fetching customer list...");
        List<User> customers = null;
        customers = userDao.getAllCustomers();

        if (customers.isEmpty()) {
            System.out.println("No customers found.");
            return;
        }

        System.out.println("----------------------------------------");
        System.out.printf("%-10s %-20s %n", "ID", "Name");
        System.out.println("----------------------------------------");
        for (User customer : customers) {
            System.out.printf("%-10d %-20s %n", customer.getUserId(), customer.getFullName());
        }
        System.out.println("----------------------------------------");
    }

    /**
     * Retrieves and displays the payment history for the gym owner.
     */
    @Override
    public void viewPayments() {
        System.out.println("Fetching payment history...");
        System.out.println("No payments found.");
    }

    /**
     * Updates a specific detail for the gym owner.
     * @param ownerId The ID of the gym owner whose details are to be updated.
     * @param choice An integer representing the detail to be updated (e.g., 1 for name, 2 for email).
     * @param newValue The new value for the selected detail.
     */
    @Override
    public void editGymOwnerDetails(int ownerId, int choice, String newValue) {
        // Fetch the existing user and gym owner data from the database
        Optional<User> userOptional = null;
        Optional<GymOwner> gymOwnerOptional = null;
        userOptional = userDao.getUserById(ownerId);
        gymOwnerOptional = gymOwnerDao.getGymOwnerById(ownerId);

        if (!userOptional.isPresent() || !gymOwnerOptional.isPresent()) {
            System.out.println("Error: User or Gym Owner not found.");
            return;
        }

        User user = userOptional.get();
        GymOwner gymOwner = gymOwnerOptional.get();

        // Update the correct field based on user choice
        try {
            switch (choice) {
                case 1: user.setFullName(newValue); break;
                case 2: user.setEmail(newValue); break;
                case 3: user.setPassword(newValue); break;
                case 4: user.setUserPhone(Long.parseLong(newValue)); break;
                case 5: user.setCity(newValue); break;
                case 6: user.setPinCode(Integer.parseInt(newValue)); break;
                case 7: gymOwner.setPan(newValue); break;
                case 8: gymOwner.setAadhaar(newValue); break;
                case 9: gymOwner.setGst(newValue); break;
                default: System.out.println("Invalid choice for update."); return;
            }
        } catch (NumberFormatException e) {
            System.err.println("Invalid format for phone number or pincode. Please enter a valid number.");
            return;
        }

        // Persist the changes to the database by calling the DAOs
        userDao.updateUser(user);
        gymOwnerDao.updateGymOwnerDetails(gymOwner);
        System.out.println("Owner details updated successfully.");
    }
}