package com.flipfit.client;

import com.flipfit.bean.Booking;
import com.flipfit.business.CustomerService;
import com.flipfit.dao.CustomerDAO;
import com.flipfit.dao.GymOwnerDAO;
import com.flipfit.dao.UserDAO;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class CustomerClient {

    private GymOwnerDAO gymOwnerDao;
    private CustomerService customerService;
    private Scanner in;
    private String loggedInCustomerId;

    public CustomerClient(CustomerDAO customerDAO, UserDAO userDao, GymOwnerDAO gymOwnerDao, String loggedInCustomerId) {
        this.gymOwnerDao = gymOwnerDao;
        this.customerService = new CustomerService(customerDAO, userDao, this.gymOwnerDao);
        this.in = new Scanner(System.in);
        this.loggedInCustomerId = loggedInCustomerId;
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
            System.out.println("5. Book a slot");
            System.out.println("6. Exit");
            System.out.print("Enter your choice: ");

            int choice = -1;
            try {
                choice = in.nextInt();
                in.nextLine(); // Consume newline
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                in.next(); // Clear the invalid input
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
                    bookaSlot();
                    break;
                case 6:
                    System.out.println("Exiting Customer Menu...");
                    exitCustomerMenu = true;
                    break;
                default:
                    System.out.println("Invalid number. Please try again.");
            }
        }
    }

    private void bookaSlot() {
        System.out.println("--- Book a Slot ---");
        try {
            System.out.print("Enter Booking ID: ");
            int bookingId = in.nextInt();

            // The customer ID is already known from the login session.
            int customerId = Integer.parseInt(loggedInCustomerId);

            System.out.print("Enter Gym ID: ");
            int gymId = in.nextInt();

            System.out.print("Enter Slot ID: ");
            int slotId = in.nextInt();
            in.nextLine(); // Consume newline

            customerService.bookSlot(bookingId, customerId, gymId, slotId);
        } catch (InputMismatchException e) {
            System.err.println("Invalid input. Please enter a valid number for all IDs.");
            in.nextLine(); // Clear the rest of the line
        }
    }

    private void viewBookedSlots() {
        System.out.println("Viewing your booked slots...");
        List<Booking> bookings = customerService.viewBookedSlots(loggedInCustomerId);

        if (bookings == null || bookings.isEmpty()) {
            System.out.println("No booked slots found.");
        } else {
            System.out.println("Booking Details:");
            for (Booking booking : bookings) {
                System.out.println("Booking ID: " + booking.getBookingId() +
                        ", Slot ID: " + booking.getSlotId() +
                        ", Gym ID: " + booking.getGymId());
            }
        }
    }

    private void viewCenters() {
        List<String[]> centers = customerService.viewCenters();
        if (centers == null || centers.isEmpty()) {
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
        int paymentType;
        try {
            paymentType = in.nextInt();
            in.nextLine(); // Consume newline
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a valid number.");
            in.next();
            return;
        }

        System.out.print("Enter account number: ");
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

            int editChoice = -1;
            try {
                editChoice = in.nextInt();
                in.nextLine(); // Consume newline
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                in.next();
                continue;
            }

            String newValue;
            switch (editChoice) {
                case 1:
                    System.out.print("Enter new name: ");
                    newValue = in.nextLine();
                    customerService.editCustomerDetails(loggedInCustomerId, 1, newValue);
                    break;
                case 2:
                    System.out.print("Enter new email: ");
                    newValue = in.nextLine();
                    customerService.editCustomerDetails(loggedInCustomerId, 2, newValue);
                    break;
                case 3:
                    System.out.print("Enter new password: ");
                    newValue = in.nextLine();
                    customerService.editCustomerDetails(loggedInCustomerId, 3, newValue);
                    break;
                case 4:
                    System.out.print("Enter new phone number: ");
                    newValue = in.nextLine();
                    customerService.editCustomerDetails(loggedInCustomerId, 4, newValue);
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