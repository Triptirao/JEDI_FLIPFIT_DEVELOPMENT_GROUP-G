package com.flipfit.client;

import com.flipfit.business.GymOwnerService;
import com.flipfit.dao.CustomerDAO;
import com.flipfit.dao.GymOwnerDAO;
import com.flipfit.dao.UserDAO;

import java.util.Scanner;
import java.util.Optional;

public class GymOwnerClient {

    private final GymOwnerService gymOwnerService;
    private final Scanner in;
    private final UserDAO userDao;
    private final CustomerDAO customerDao;
    private final GymOwnerDAO gymOwnerDao;
    private final String loggedInOwnerId;

    // The constructor should receive the DAOs and the logged-in user ID
    public GymOwnerClient(GymOwnerDAO gymOwnerDAO, UserDAO userDao, CustomerDAO customerDao, String loggedInOwnerId) {
        this.gymOwnerDao = gymOwnerDAO;
        this.userDao = userDao;
        this.customerDao = customerDao;
        this.loggedInOwnerId = loggedInOwnerId;
        this.gymOwnerService = new GymOwnerService(gymOwnerDAO, userDao, customerDao);
        this.in = new Scanner(System.in);
    }

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
                choice = in.nextInt();
                in.nextLine(); // Consume newline
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter a number.");
                in.next();
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
                    return;
                default:
                    System.out.println("Invalid number. Please try again.");
            }
        }
    }

    private void addCentre() {
        Optional<String[]> ownerData = userDao.getUserById(loggedInOwnerId);
        if (ownerData.isPresent() && ownerData.get().length > 11 && ownerData.get()[11].equals("true")) {
            System.out.print("Enter gym ID: ");
            String gymId = in.nextLine();
            System.out.print("Enter gym name: ");
            String name = in.nextLine();
            System.out.print("Enter gym capacity: ");
            String capacity = in.nextLine();
            System.out.print("Enter gym city: ");
            String city = in.nextLine();
            System.out.print("Enter gym state: ");
            String state = in.nextLine();
            String[] newGymData = new String[]{gymId, loggedInOwnerId, name, capacity, "false", city, state};
            gymOwnerService.addCentre(newGymData);
            System.out.println("Gym Centre " + name + " added and awaiting admin approval.");
        } else {
            System.out.println("You are not yet approved to add a gym centre. Please wait for an admin to approve your account.");
        }
    }

    private void viewGymDetails() {
        System.out.println("Viewing your gym details...");
        gymOwnerService.viewGymDetails(loggedInOwnerId);
    }

    private void viewCustomers() {
        System.out.println("Viewing customers...");
        gymOwnerService.viewCustomers();
    }

    private void viewPayments() {
        System.out.println("Viewing payment history...");
        gymOwnerService.viewPayments();
    }

    private void editDetails() {
        boolean continueEditing = true;
        while (continueEditing) {
            System.out.println("\n--- Edit My Details ---");
            System.out.println("1. Change Name");
            System.out.println("2. Change Email");
            System.out.println("3. Change Password");
            System.out.println("4. Change Phone Number");
            System.out.println("5. Change City");
            System.out.println("6. Change Pincode");
            System.out.println("7. Change PAN");
            System.out.println("8. Change Aadhaar");
            System.out.println("9. Change GST");
            System.out.println("10. Back to main menu");
            System.out.print("Enter your choice: ");
            int editChoice = in.nextInt();
            in.nextLine(); // Consume newline
            String newValue;

            switch (editChoice) {
                case 1:
                    System.out.print("Enter new name: ");
                    newValue = in.nextLine();
                    gymOwnerService.editGymOwnerDetails(loggedInOwnerId, 1, newValue);
                    break;
                case 2:
                    System.out.print("Enter new email: ");
                    newValue = in.nextLine();
                    gymOwnerService.editGymOwnerDetails(loggedInOwnerId, 2, newValue);
                    break;
                case 3:
                    System.out.print("Enter new password: ");
                    newValue = in.nextLine();
                    gymOwnerService.editGymOwnerDetails(loggedInOwnerId, 3, newValue);
                    break;
                case 4:
                    System.out.print("Enter new phone number: ");
                    newValue = in.nextLine();
                    gymOwnerService.editGymOwnerDetails(loggedInOwnerId, 4, newValue);
                    break;
                case 5:
                    System.out.print("Enter new city: ");
                    newValue = in.nextLine();
                    gymOwnerService.editGymOwnerDetails(loggedInOwnerId, 5, newValue);
                    break;
                case 6:
                    System.out.print("Enter new pincode: ");
                    newValue = in.nextLine();
                    gymOwnerService.editGymOwnerDetails(loggedInOwnerId, 6, newValue);
                    break;
                case 7:
                    System.out.print("Enter new PAN: ");
                    newValue = in.nextLine();
                    gymOwnerService.editGymOwnerDetails(loggedInOwnerId, 7, newValue);
                    break;
                case 8:
                    System.out.print("Enter new Aadhaar: ");
                    newValue = in.nextLine();
                    gymOwnerService.editGymOwnerDetails(loggedInOwnerId, 8, newValue);
                    break;
                case 9:
                    System.out.print("Enter new GST: ");
                    newValue = in.nextLine();
                    gymOwnerService.editGymOwnerDetails(loggedInOwnerId, 9, newValue);
                    break;
                case 10:
                    continueEditing = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
}