package com.flipfit.client;

import com.flipfit.business.CustomerService;
import com.flipfit.dao.CustomerDAO;
import com.flipfit.dao.GymOwnerDAO;
import com.flipfit.dao.UserDAO;
import com.flipfit.dao.GymCentreDAO;

import java.util.List;
import java.util.Scanner;

public class CustomerClient {

    private GymOwnerDAO gymOwnerDao;
    private CustomerService customerService;
    private Scanner in;
    private String loggedInCustomerId; // To track the logged-in user
    private GymCentreDAO gymCentreDao;

    // The constructor now receives DAOs as dependencies
    public CustomerClient(CustomerDAO customerDAO, UserDAO userDao, String loggedInCustomerId, GymCentreDAO gymCentreDao) {
        this.customerService = new CustomerService(customerDAO, userDao, this.gymOwnerDao, gymCentreDao);
        this.in = new Scanner(System.in);
        this.loggedInCustomerId = loggedInCustomerId;
        this.gymCentreDao = gymCentreDao;
    }

    public void customerPage() {
        boolean exitCustomerMenu = false;
        while (!exitCustomerMenu) {
            System.out.println("----------------------------------------");
            System.out.println("            Customer Menu");
            System.out.println("----------------------------------------");
            System.out.println("1. View Booked Slots");
            System.out.println("2. View Gym Centers");
            System.out.println("3. Make Payments");
            System.out.println("4. Edit Details");
            System.out.println("5. Exit");
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
                    viewBookedSlots();
                    break;
                case 2:
                    viewCenters();
                    break;
                case 3:
                    makePayments();
                    break;
                case 4:
                    editDetails();
                    break;
                case 5:
                    System.out.println("Exiting Customer Menu...");
                    exitCustomerMenu = true;
                    break;
                default:
                    System.out.println("Invalid number. Please try again.");
            }
        }
    }

    private void viewBookedSlots() {
        System.out.println("Viewing your booked slots...");
        List<String[]> bookings = customerService.viewBookedSlots(loggedInCustomerId);
        if (bookings.isEmpty()) {
            System.out.println("No booked slots found.");
        } else {
            System.out.println("Booking Details:");
            for (String[] booking : bookings) {
                System.out.println("Booking ID: " + booking[0] + ", Slot ID: " + booking[2] + ", Centre ID: " + booking[3]);
            }
        }
    }

    private void viewCenters() {
        List<String[]> centers = customerService.viewCenters();
        if (centers.isEmpty()) {
            System.out.println("No gym centers found.");
        } else {
            System.out.println("Gym Centers:");
            for (String[] center : centers) {
                System.out.println("ID: " + center[0] + ", Name: " + center[2] + ", City: " + center[6]);
            }
        }
    }

    private void makePayments() {
        System.out.println("Initiating payment process...");
        System.out.print("Enter payment type (1 for Credit Card, 2 for Debit Card, etc.): ");
        int paymentType = in.nextInt();
        in.nextLine(); // Consume newline
        System.out.print("Enter payment info: ");
        String paymentInfo = in.nextLine();

        customerService.makePayments(paymentType, paymentInfo);
    }

    private void editDetails() {
        boolean continueEditing = true;
        while (continueEditing) {
            System.out.println("\n--- Edit My Details ---");
            System.out.println("1. Change Name");
            System.out.println("2. Change Email");
            System.out.println("3. Change Password");
            System.out.println("4. Change Phone Number");
            System.out.println("5. Back to main menu");
            System.out.print("Enter your choice: ");
            int editChoice = in.nextInt();
            in.nextLine(); // Consume newline

            String newValue;
            switch (editChoice) {
                case 1:
                    System.out.print("Enter new name: ");
                    newValue = in.nextLine();
                    customerService.editCustomerDetails(loggedInCustomerId, 2, newValue);
                    break;
                case 2:
                    System.out.print("Enter new email: ");
                    newValue = in.nextLine();
                    customerService.editCustomerDetails(loggedInCustomerId, 3, newValue);
                    break;
                case 3:
                    System.out.print("Enter new password: ");
                    newValue = in.nextLine();
                    customerService.editCustomerDetails(loggedInCustomerId, 4, newValue);
                    break;
                case 4:
                    System.out.print("Enter new phone number: ");
                    newValue = in.nextLine();
                    customerService.editCustomerDetails(loggedInCustomerId, 5, newValue);
                    break;
                case 5:
                    continueEditing = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
}
