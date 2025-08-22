package com.flipfit.business;

import com.flipfit.bean.GymCentre;
import com.flipfit.bean.GymOwner;
import com.flipfit.bean.User;
import com.flipfit.dao.AdminDAO;
import com.flipfit.dao.CustomerDAO;
import com.flipfit.dao.GymOwnerDAO;
import com.flipfit.dao.UserDAO;
import com.flipfit.exception.*;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * The AdminService class provides the business logic for all administrative tasks.
 * It interacts with various DAO classes to perform operations such as
 * approving gym requests, viewing users, and deleting entities.
 *
 * @author
 */
public class AdminService implements adminInterface {

    private static final Scanner in = new Scanner(System.in);
    private final AdminDAO adminDao;
    private final UserDAO userDao;
    private final GymOwnerDAO gymOwnerDao;

    public AdminService(AdminDAO adminDao, UserDAO userDao, CustomerDAO customerDAO, GymOwnerDAO gymOwnerDao) {
        this.adminDao = adminDao;
        this.userDao = userDao;
        this.gymOwnerDao = gymOwnerDao;
    }

    /**
     * Approves a pending gym center request.
     * @param gymId The ID of the gym center to approve.
     */
    @Override
    public void approveGymRequest(int gymId) throws MismatchinputException {
        try {
            adminDao.approveGymRequest(gymId);
            System.out.println("Gym with ID " + gymId + " approved successfully.");
        } catch (Exception e) {
            throw new MismatchinputException("Failed to approve gym request with ID " + gymId + ". " + e.getMessage());
        }
    }

    /**
     * Approves a pending gym owner registration request.
     * @param email The email of the gym owner to approve.
     */
    @Override
    public void approveGymOwnerRequest(String email) throws MismatchinputException {
        try {
            adminDao.approveGymOwnerRequest(email);
            System.out.println("Gym owner with email " + email + " approved successfully.");
        } catch (Exception e) {
            throw new MismatchinputException("Failed to approve gym owner request with email " + email + ". " + e.getMessage());
        }
    }

    /**
     * Fetches and displays a list of all pending gym requests.
     */
    @Override
    public void viewPendingGyms() {
        System.out.println("Fetching all pending gym requests...");
        List<GymCentre> pendingGyms = adminDao.getPendingGymRequests();
        if (pendingGyms.isEmpty()) {
            System.out.println("No pending gym requests found.");
            return;
        }

        System.out.println("--------------------------------------------------");
        System.out.printf("%-10s %-20s %-15s%n", "ID", "Name", "City");
        System.out.println("--------------------------------------------------");
        for (GymCentre gym : pendingGyms) {
            System.out.printf("%-10d %-20s %-15s%n", gym.getCentreId(), gym.getCentreName(), gym.getCity());
        }
        System.out.println("--------------------------------------------------");
    }

    /**
     * Fetches and displays a list of all pending gym owner requests.
     */
    @Override
    public void viewPendingGymOwners() {
        System.out.println("Fetching all pending gym owner requests...");
        List<User> pendingOwners = adminDao.getPendingGymOwnerRequests();
        if (pendingOwners.isEmpty()) {
            System.out.println("No pending gym owners found.");
            return;
        }

        System.out.println("--------------------------------------------------");
        System.out.printf("%-30s %-20s %-15s%n", "Email", "Name", "Approved");
        System.out.println("--------------------------------------------------");
        for (User owner : pendingOwners) {
            Optional<GymOwner> gymOwnerOptional = gymOwnerDao.getGymOwnerById(owner.getUserId());
            String approvedStatus = gymOwnerOptional.isPresent() && gymOwnerOptional.get().isApproved() ? "Yes" : "No";
            System.out.printf("%-30s %-20s %-15s%n", owner.getEmail(), owner.getFullName(), approvedStatus);
        }
        System.out.println("--------------------------------------------------");
    }

    /**
     * Fetches and displays a list of all approved and registered gyms.
     */
    @Override
    public void viewAllGyms() {
        System.out.println("Fetching all registered gym centers...");
        List<GymCentre> allGyms = adminDao.getAllGyms();
        if (allGyms.isEmpty()) {
            System.out.println("No registered gym centers found.");
            return;
        }

        System.out.println("Displaying all registered gym centers:");
        for (GymCentre gym : allGyms) {
            System.out.println("------------------------------------");
            System.out.println("Centre ID: " + gym.getCentreId());
            System.out.println("Owner ID: " + gym.getOwnerId());
            System.out.println("Name: " + gym.getCentreName());
            System.out.println("Capacity: " + gym.getCapacity());
            System.out.println("City: " + gym.getCity());
            System.out.println("State: " + gym.getState());
            System.out.println("------------------------------------");
        }
    }

    /**
     * Fetches and displays a list of all approved and registered gym owners.
     */
    @Override
    public void viewAllGymOwners() {
        System.out.println("Fetching all registered gym owners...");
        List<User> allOwners = adminDao.getAllGymOwners();
        if (allOwners.isEmpty()) {
            System.out.println("No registered gym owners found.");
            return;
        }

        System.out.println("Displaying all registered gym owners:");
        for (User owner : allOwners) {
            Optional<GymOwner> gymOwnerOptional = gymOwnerDao.getGymOwnerById(owner.getUserId());
            if (gymOwnerOptional.isPresent()) {
                GymOwner gymOwner = gymOwnerOptional.get();
                System.out.println("------------------------------------");
                System.out.println("User ID: " + owner.getUserId());
                System.out.println("Full Name: " + owner.getFullName());
                System.out.println("Email: " + owner.getEmail());
                System.out.println("Phone number: " + owner.getUserPhone());
                System.out.println("City: " + owner.getCity());
                System.out.println("Pincode: " + owner.getPinCode());
                System.out.println("PAN: " + gymOwner.getPan());
                System.out.println("Aadhaar: " + gymOwner.getAadhaar());
                System.out.println("GST: " + gymOwner.getGst());
                System.out.println("------------------------------------");
            }
        }
    }

    /**
     * Fetches and displays a list of all registered customers.
     */
    @Override
    public void viewAllCustomers(){
        System.out.println("Fetching all registered customers...");
        List<User> allCustomers = adminDao.getAllCustomers();
        if (allCustomers.isEmpty()) {
            System.out.println("No registered customers found.");
            return;
        }

        System.out.println("Displaying all registered customers:");
        for (User customer : allCustomers) {
            System.out.println("------------------------------------");
            System.out.println("User ID: " + customer.getUserId());
            System.out.println("Full Name: " + customer.getFullName());
            System.out.println("Email: " + customer.getEmail());
            System.out.println("Phone number: " + customer.getUserPhone());
            System.out.println("City: " + customer.getCity());
            System.out.println("Pincode: " + customer.getPinCode());
            System.out.println("------------------------------------");
        }
    }

    /**
     * Deletes a user from the system by their ID.
     * @param userId The ID of the user to delete.
     */
    @Override
    public void deleteUserById(int userId) throws UnableToDeleteUserException {
        try {
            adminDao.deleteUser(userId);
            System.out.println("User with ID: " + userId + " deleted successfully.");
        } catch (Exception e) {
            throw new UnableToDeleteUserException("Failed to delete user with ID " + userId + ". " + e.getMessage());
        }
    }

    /**
     * Deletes a gym from the system by its ID.
     * @param gymId The ID of the gym to delete.
     */
    @Override
    public void deleteGymById(int gymId) throws MismatchinputException {
        try {
            adminDao.deleteGym(gymId);
            System.out.println("Gym with ID: " + gymId + " deleted successfully.");
        } catch (Exception e) {
            throw new MismatchinputException("Failed to delete gym with ID " + gymId + ". " + e.getMessage());
        }
    }
}