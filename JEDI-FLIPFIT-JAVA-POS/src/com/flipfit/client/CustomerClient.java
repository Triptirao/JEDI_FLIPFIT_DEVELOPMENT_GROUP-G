package com.flipfit.client;

import com.flipfit.bean.Booking;
import com.flipfit.bean.GymCentre;
import com.flipfit.business.CustomerService;
import com.flipfit.dao.*;
import com.flipfit.exception.AccessDeniedException;
import com.flipfit.exception.AuthenticationException;
import com.flipfit.exception.DuplicateEntryException;
import com.flipfit.exception.IndexOutOfBoundsException;
import com.flipfit.exception.MismatchinputException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class CustomerClient {

    private GymOwnerDAO gymOwnerDao;
    private final CustomerService customerService;
    private Scanner in;
    private int loggedInCustomerId; // To track the logged-in user


    // The constructor now receives DAOs as dependencies
    public CustomerClient(CustomerDAO customerDAO, UserDAO userDao, int loggedInCustomerId) {
        // Initialize CustomerService with the necessary DAO objects.
        this.customerService = new CustomerService(userDao,customerDAO, this.gymOwnerDao);
        // Initialize Scanner for user input.
        this.in = new Scanner(System.in);
        // Store the ID of the currently logged-in customer.
        this.loggedInCustomerId = loggedInCustomerId;

    }

    /**
     * Displays the main menu for the customer and handles user input
     * to navigate to different functionalities.
     * @author
     */

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

            int choice;
            try {
                // Read the user's menu choice.
                choice = in.nextInt();
                in.nextLine(); // Consume newline left-over after nextInt().
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                // Clear the invalid input from the scanner to prevent an infinite loop.
                in.nextLine();
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

    /**
     * Prompts the user for details to book a slot and calls the
     * business service to perform the booking.
     */
    private void bookaSlot() {
        System.out.println("--- Book a Slot ---");
        try {
            System.out.print("Enter Gym ID: ");
            int gymId = in.nextInt();
            System.out.print("Enter Slot ID: ");
            int slotId = in.nextInt();
            in.nextLine(); // Consume newline
            System.out.print("Enter Booking Date (YYYY-MM-DD): ");
            String dateStr = in.nextLine();

            // Parse the date from the user input.
            LocalDate bookingDate = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            // Use the existing service object to book the slot.
            customerService.bookSlot(loggedInCustomerId, gymId, slotId, bookingDate);
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a number for ID.");
            in.nextLine(); // Clear the invalid input
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format. Please use YYYY-MM-DD.");
        } catch (DuplicateEntryException | AccessDeniedException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Retrieves and displays the booked slots for the logged-in customer.
     */
    private void viewBookedSlots() {
        System.out.println("Viewing your booked slots...");
        try {
            // Call the service layer to get the list of bookings for the customer.
            List<Booking> bookings = customerService.viewBookedSlots(loggedInCustomerId);

            if (bookings.isEmpty()) {
                System.out.println("No booked slots found.");
            } else {
                System.out.println("Booking Details:");
                // Iterate and print the details of each booking.
                for (Booking booking : bookings) {
                    System.out.println("Booking ID: " + booking.getBookingId() +
                            ", Slot ID: " + booking.getSlotId() +
                            ", Gym ID: " + booking.getGymId());
                }
            }
        } catch (Exception e) {
            System.out.println("An error occurred while viewing booked slots: " + e.getMessage());
        }
    }

    /**
     * Retrieves and displays a list of all available gym centers.
     */
    private void viewCenters() {
        try {
            // Call the service layer to retrieve all gym centers.
            List<GymCentre> centers = customerService.viewCenters();
            if (centers.isEmpty()) {
                System.out.println("No gym centers found.");
            } else {
                System.out.println("Gym Centers:");
                // Iterate through the list of GymCentre objects and display their details.
                for (GymCentre center : centers) {
                    System.out.println("ID: " + center.getCentreId() +
                            ", Name: " + center.getCentreName() +
                            ", City: " + center.getCity());
                }
            }
        } catch (Exception e) {
            System.out.println("An error occurred while viewing centers: " + e.getMessage());
        }
    }

    /**
     * Prompts for payment details and calls the service to process the payment.
     */
    private void makePayments() {
        System.out.println("Initiating payment process...");
        try {
            System.out.print("Enter payment type (1 for Credit Card, 2 for Debit Card, etc.): ");
            int paymentType = in.nextInt();
            in.nextLine(); // Consume newline
            System.out.print("Enter account number: ");
            String paymentInfo = in.nextLine();

            // Pass the logged-in customer's ID and payment info to the service method.
            customerService.makePayments(loggedInCustomerId, paymentType, paymentInfo);
        } catch (InputMismatchException e) {
            System.out.println("Invalid input for payment type. Please enter a number.");
            in.nextLine();
        } catch (Exception e) {
            System.out.println("An error occurred during payment: " + e.getMessage());
        }
    }

    /**
     * Provides a sub-menu for the customer to edit their personal details.
     */
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
            int editChoice;
            try {
                editChoice = in.nextInt();
                in.nextLine(); // Consume newline
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                in.nextLine();
                continue;
            }

            String newValue;
            try {
                switch (editChoice) {
                    case 1:
                        System.out.print("Enter new name: ");
                        newValue = in.nextLine();
                        // Call the service method to update the customer's name.
                        customerService.editCustomerDetails(loggedInCustomerId, 1, newValue);
                        break;
                    case 2:
                        System.out.print("Enter new email: ");
                        newValue = in.nextLine();
                        // Call the service method to update the customer's email.
                        customerService.editCustomerDetails(loggedInCustomerId, 2, newValue);
                        break;
                    case 3:
                        System.out.print("Enter new password: ");
                        newValue = in.nextLine();
                        // Call the service method to update the customer's password.
                        customerService.editCustomerDetails(loggedInCustomerId, 3, newValue);
                        break;
                    case 4:
                        System.out.print("Enter new phone number: ");
                        newValue = in.nextLine();
                        // Call the service method to update the customer's phone number.
                        customerService.editCustomerDetails(loggedInCustomerId, 4, newValue);
                        break;
                    case 5:
                        // Exit the editing loop and return to the main customer menu.
                        continueEditing = false;
                        break;
                    default:
                        throw new MismatchinputException("Invalid choice. Please try again.");
                }
            } catch (MismatchinputException e) {
                System.out.println("Error: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("An unexpected error occurred: " + e.getMessage());
            }
        }
    }
}