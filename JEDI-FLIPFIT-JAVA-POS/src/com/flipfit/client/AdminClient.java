package com.flipfit.client;

import com.flipfit.business.AdminService;
import com.flipfit.dao.AdminDAO;
import com.flipfit.dao.CustomerDAO;
import com.flipfit.dao.GymOwnerDAO;
import com.flipfit.dao.UserDAO;


import java.util.Scanner;

/**
 * The AdminClient class provides a command-line interface for an administrator
 * to perform various administrative tasks. It interacts with the AdminService
 * to manage gyms, gym owners, and customers.
 * * @author
 */
public class AdminClient {

    private final AdminService adminService;
    private final Scanner in;

    /**
     * Constructs an AdminClient with necessary data access objects.
     * * @param adminDao     DAO for admin-specific operations.
     * @param userDao      DAO for user-related operations.
     * @param customerDao  DAO for customer-related operations.
     * @param gymOwnerDao  DAO for gym owner-related operations.
     */
    public AdminClient(AdminDAO adminDao, UserDAO userDao, CustomerDAO customerDao, GymOwnerDAO gymOwnerDao) {
        this.adminService = new AdminService(adminDao, userDao,customerDao, gymOwnerDao);
        this.in = new Scanner(System.in);
    }

    /**
     * Displays the admin menu and handles user input for various administrative actions.
     * The loop continues until the user chooses to exit.
     */
    public void adminPage() {
        boolean exitAdminMenu = false;
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

            // Read the user's choice and handle potential InputMismatchException
            int choice = in.nextInt();
            in.nextLine(); // Consume the newline character left after nextInt()

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
                    // Parse the gym ID and handle potential NumberFormatException
                    adminService.approveGymRequest(Integer.parseInt(gymId));
                    break;
                case 8:
                    System.out.print("Enter user ID to delete: ");
                    String userId = in.nextLine();
                    // Parse the user ID and handle potential NumberFormatException
                    adminService.deleteUserById(Integer.parseInt(userId));
                    break;
                case 9:
                    System.out.print("Enter gym ID to delete: ");
                    String gymIdToDelete = in.nextLine();
                    // Parse the gym ID and handle potential NumberFormatException
                    adminService.deleteGymById(Integer.parseInt(gymIdToDelete));
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