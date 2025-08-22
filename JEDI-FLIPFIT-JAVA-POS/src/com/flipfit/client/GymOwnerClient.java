package com.flipfit.client;

import com.flipfit.bean.GymCentre;
import com.flipfit.bean.GymOwner;
import com.flipfit.bean.User;
import com.flipfit.business.GymOwnerService;
import com.flipfit.dao.CustomerDAO;
import com.flipfit.dao.GymOwnerDAO;
import com.flipfit.dao.UserDAO;

import java.util.Optional;
import java.util.Scanner;

/**
 * Client class for the Gym Owner user role. This class handles the
 * user interface and interactions for gym owners, providing a menu-driven
 * system to perform actions such as adding gym centers, viewing details,
 * and editing their profile.
 */
public class GymOwnerClient {

    private final GymOwnerService gymOwnerService;
    private final Scanner in;
    private final UserDAO userDao;
    private final GymOwnerDAO gymOwnerDao;
    private final int loggedInOwnerId;

    /**
     * Constructs a GymOwnerClient object.
     *
     * @param userDao The DAO for user data access.
     * @param customerDao The DAO for customer data access.
     * @param gymOwnerDAO The DAO for gym owner data access.
     * @param loggedInOwnerId The user ID of the currently logged-in gym owner.
     */
    public GymOwnerClient(UserDAO userDao, CustomerDAO customerDao,GymOwnerDAO gymOwnerDAO, int loggedInOwnerId) {
        this.gymOwnerDao = gymOwnerDAO;
        this.userDao = userDao;
        this.loggedInOwnerId = loggedInOwnerId;
        // Initialize the service layer with the required DAOs.
        this.gymOwnerService = new GymOwnerService(userDao, customerDao, gymOwnerDAO);
        this.in = new Scanner(System.in);
    }

    /**
     * Displays the main menu for the gym owner and handles their choices
     * for various operations.
     */
    public void gymOwnerPage() {
        boolean exitOwnerMenu = false;
        while (!exitOwnerMenu) {
            System.out.println("----------------------------------------");
            System.out.println("            Gym Owner Menu");
            System.out.println("----------------------------------------");
            System.out.println("1. Add a new Gym Centre");
            System.out.println("2. View Gym Details");
            System.out.println("3. View Customers");
            System.out.println("4. View Payments");
            System.out.println("5. Edit Details");
            System.out.println("6. Exit");
            System.out.print("Enter your choice: ");

            int choice;
            try {
                // Read the user's menu choice.
                choice = in.nextInt();
                in.nextLine(); // Consume the newline character.
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter a number.");
                in.next(); // Clear the invalid input from the scanner.
                continue;
            }

            switch (choice) {
                case 1:
                    addCentre();
                    break;
                case 2:
                    viewGymDetails();
                    break;
                case 3:
                    viewCustomers();
                    break;
                case 4:
                    viewPayments();
                    break;
                case 5:
                    editDetails();
                    break;
                case 6:
                    System.out.println("Exiting Gym Owner Menu...");
                    return; // Exit the method and the loop.
                default:
                    System.out.println("Invalid number. Please try again.");
            }
        }
    }

    /**
     * Prompts the gym owner for details to add a new gym centre.
     * The method first checks if the owner's account is approved.
     */
    private void addCentre() {
        // Retrieve the gym owner's data to check for approval status.
        Optional<GymOwner> ownerData = gymOwnerDao.getGymOwnerById(loggedInOwnerId);
        if (ownerData.isPresent()) {
            GymOwner owner = ownerData.get();
            // Check if the gym owner is approved to add a centre.
            if (owner.isApproved()) {
                System.out.print("Enter gym name: ");
                String name = in.nextLine();
                System.out.print("Enter gym capacity: ");
                int capacity = in.nextInt();
                in.nextLine();
                System.out.print("Enter gym city: ");
                String city = in.nextLine();
                System.out.print("Enter gym state: ");
                String state = in.nextLine();
                System.out.print("Enter gym pin code: ");
                String pincode = in.nextLine();

                // Create a new GymCentre object and call the service to add it.
                // The centre is initially marked as not approved (false) by default.
                GymCentre newGym = new GymCentre(owner.getUserId(), name, null, capacity, false, city, state, pincode, null);
                gymOwnerService.addCentre(newGym);
                System.out.println("Gym Centre " + name + " added and awaiting admin approval.");
            } else {
                System.out.println("You are not yet approved to add a gym centre. Please wait for an admin to approve your account.");
            }
        } else {
            System.out.println("Error: Gym Owner data not found.");
        }
    }

    /**
     * Calls the service layer to display the gym details associated with the
     * logged-in gym owner.
     */
    private void viewGymDetails() {
        System.out.println("Viewing your gym details...");
        gymOwnerService.viewGymDetails(loggedInOwnerId);
    }

    /**
     * Calls the service layer to display the list of customers.
     */
    private void viewCustomers() {
        System.out.println("Viewing customers...");
        gymOwnerService.viewCustomers();
    }

    /**
     * Calls the service layer to display the payment history.
     */
    private void viewPayments() {
        System.out.println("Viewing payment history...");
        gymOwnerService.viewPayments();
    }

    /**
     * Provides a sub-menu for the gym owner to edit their personal details.
     */
    private void editDetails() {
        boolean continueEditing = true;
        while (continueEditing) {
            System.out.println("\n--- Edit My Details ---");
            System.out.println("1. Change Name");
            System.out.println("2. Change Email");
            System.out.println("3. Change Password");
            System.out.println("4. Change Phone Number");
            System.out.println("5. Change City");
            System.out.println("6. Change Pin Code");
            System.out.println("7. Change PAN");
            System.out.println("8. Change Aadhaar");
            System.out.println("9. Change GST");
            System.out.println("10. Back to main menu");
            System.out.print("Enter your choice: ");
            int editChoice = in.nextInt();
            in.nextLine(); // Consume newline

            if (editChoice == 10) {
                continueEditing = false;
                break;
            }

            System.out.print("Enter new value: ");
            String newValue = in.nextLine();

            // Call the service method to update the gym owner's details based on their choice.
            gymOwnerService.editGymOwnerDetails(loggedInOwnerId, editChoice, newValue);
        }
    }
}