package com.flipfit.business;

import com.flipfit.dao.AdminDAO;
import com.flipfit.dao.CustomerDAO;
import com.flipfit.dao.GymOwnerDAO;
import com.flipfit.dao.UserDAO;


import java.util.List;
import java.util.Scanner;

public class AdminService implements adminInterface{

    private static final Scanner in = new Scanner(System.in);
    private AdminDAO adminDao;
    private CustomerDAO customerDao;
    private UserDAO userDao;
    private GymOwnerDAO gymOwnerDao;

    public AdminService() {
        this.adminDao = new AdminDAO();
        this.customerDao = new CustomerDAO();
    }

    public AdminService(AdminDAO adminDao, CustomerDAO customerDao, UserDAO userDao, GymOwnerDAO gymOwnerDao) {
        this.adminDao = adminDao;
        this.customerDao = customerDao;
        this.userDao = userDao;
        this.gymOwnerDao = gymOwnerDao;
    }

    /**
     * Approves a pending gym center request.
     * @param gymId The ID of the gym center to approve.
     */
    public void approveGymRequest(String gymId) {
        adminDao.approveGymRequest(gymId);
        System.out.println("Gym with ID " + gymId + " approved successfully.");
    }

    /**
     * Approves a pending gym owner registration request.
     * @param email The email of the gym owner to approve.
     */
    public void approveGymOwnerRequest(String email) {
        adminDao.approveGymOwnerRequest(email);
        System.out.println("Gym owner with email " + email + " approved successfully.");
    }

    public void viewPendingGyms() {
        System.out.println("Fetching all pending gym requests...");
        List<String[]> pendingGyms = adminDao.getPendingGymRequests();
        if (pendingGyms.isEmpty()) {
            System.out.println("No pending gym requests found.");
            return;
        }

        System.out.println("--------------------------------------------------");
        System.out.printf("%-10s %-20s%n", "ID", "Name");
        System.out.println("--------------------------------------------------");
        for (String[] gym : pendingGyms) {
            System.out.printf("%-10s %-20s%n", gym[0], gym[1]);
        }
        System.out.println("--------------------------------------------------");
    }

    // Edited method to fix the ArrayIndexOutOfBoundsException
    public void viewPendingGymOwners() {
        System.out.println("Fetching all pending gym owner requests...");
        List<String[]> pendingOwners = adminDao.getPendingGymOwnerRequests();
        if (pendingOwners.isEmpty()) {
            System.out.println("No pending gym owners found.");
            return;
        }

        System.out.println("--------------------------------------------------");
        System.out.printf("%-30s %-20s%n", "Email", "Name");
        System.out.println("--------------------------------------------------");
        for (String[] owner : pendingOwners) {
            System.out.printf("%-30s %-20s%n", owner[2], owner[1]);
        }
        System.out.println("--------------------------------------------------");
    }

    public void viewAllGyms() {
        System.out.println("Fetching all registered gym centers...");
        List<String[]> allGyms = adminDao.getAllGyms();

        System.out.println("Displaying all registered gym centers:");
        for (String[] gym : allGyms) {
            System.out.println("------------------------------------");
            System.out.println("Centre ID: " + gym[0]);
            System.out.println("Name: " + gym[1]);
            System.out.println("City: " + gym[2]);
            System.out.println("------------------------------------");
        }
    }

    public void viewAllGymOwners() {
        System.out.println("Fetching all registered gym owners...");
        List<String[]> allOwners = adminDao.getAllGymOwners();

        System.out.println("Displaying all registered gym owners:");
        for (String[] owner : allOwners) {
            System.out.println("------------------------------------");
            System.out.println("User ID: " + owner[0]);
            System.out.println("Full Name: " + owner[1]);
            System.out.println("Email: " + owner[2]);
            System.out.println("------------------------------------");
        }
    }

    public void viewAllCustomers(){
        System.out.println("Fetching all registered customers...");
        List<String[]> allCustomers = adminDao.getAllCustomers();

        System.out.println("Displaying all registered customers:");
        for (String[] customer : allCustomers) {
            System.out.println("------------------------------------");
            System.out.println("User ID: " + customer[0]);
            System.out.println("Full Name: " + customer[1]);
            System.out.println("Email: " + customer[2]);
            System.out.println("Phone number: " + customer[3]);
            System.out.println("City: " + customer[4]);
            System.out.println("Pincode: " + customer[5]);
            System.out.println("------------------------------------");
        }
    }

    public void deleteUserById(int userId) {
        adminDao.deleteUser(String.valueOf(userId));
        System.out.println("User with ID: " + userId + " deleted successfully.");
    }

    public void deleteGymById(int gymId) {
        adminDao.deleteGym(String.valueOf(gymId));
        System.out.println("Gym with ID: " + gymId + " deleted successfully.");
    }

    /**
     * This method handles the entire admin menu flow with a while loop.
     */
    public static void displayAdminMenu() {
        boolean exitAdminMenu = false;
        AdminService adminService = new AdminService();
        while (!exitAdminMenu) {
            System.out.println("\n*** Welcome, Admin! ***");
            System.out.println("1. View All Gyms");
            System.out.println("2. View All Pending Gyms");
            System.out.println("3. View All Gym Owners");
            System.out.println("4. View All Pending Gym Owners");
            System.out.println("5. View All Customers");
            System.out.println("6. Approve Gym Owner");
            System.out.println("7. Approve Gym Centre");
            System.out.println("8. Delete User by id");
            System.out.println("9. Delete Gym by id");
            System.out.println("10. Exit");
            System.out.print("Enter your choice: ");

            int choice = in.nextInt();
            in.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    adminService.viewAllGyms();
                    break;
                case 2:
                    adminService.viewPendingGyms();
                    break;
                case 3:
                    adminService.viewAllGymOwners();
                    break;
                case 4:
                    adminService.viewPendingGymOwners();
                    break;
                case 5:
                    adminService.viewAllCustomers();
                    break;
                case 6:
                    System.out.print("Enter owner email to approve: ");
                    String email = in.nextLine();
                    adminService.approveGymOwnerRequest(email);
                    break;
                case 7:
                    System.out.print("Enter gym ID to approve: ");
                    String gymId = in.nextLine();
                    adminService.approveGymRequest(gymId);
                    break;
                case 8:
                    System.out.print("Enter user ID to delete: ");
                    int userId = in.nextInt();
                    in.nextLine(); // Consume newline
                    adminService.deleteUserById(userId);
                    break;
                case 9:
                    System.out.print("Enter gym ID to delete: ");
                    int gymIdToDelete = in.nextInt();
                    in.nextLine(); // Consume newline
                    adminService.deleteGymById(gymIdToDelete);
                    break;
                case 10:
                    exitAdminMenu = true;
                    System.out.println("Exiting Admin menu.");
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }
}