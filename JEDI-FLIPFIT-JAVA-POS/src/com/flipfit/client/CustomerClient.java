package com.flipfit.client;

import com.flipfit.bean.Booking;
import com.flipfit.bean.GymCentre;
import com.flipfit.bean.User;
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
import java.util.Optional;
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
            System.out.println("Invalid input. Please enter a number for gym ID and slot ID.");
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
                            ", Gym ID: " + booking.getGymId() +
                            ", Booking Status: " + booking.getBookingStatus() +
                            ", Booking Date: " + booking.getBookingDate().toString() +
                            ", Date and Time of Booking: " + booking.getDateAndTimeOfBooking().toString());
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
            Optional<Integer> balance = Optional.ofNullable(customerService.retrieveBalance(loggedInCustomerId));

            if (balance.isPresent()) {
                int balanceData = balance.get();
                System.out.println("Current wallet balance: " + balanceData);

                int newBalance = 0;
                try {
                    System.out.println("Enter amount to add to wallet: ");
                    newBalance = in.nextInt();
                    in.nextLine(); // Consume newline
                } catch (InputMismatchException e) {
                    System.out.println("Invalid amount. Please enter a number.");
                    in.nextLine(); // Clear the invalid input
                    return;
                }
                customerService.makePayments(loggedInCustomerId, newBalance);
                System.out.println("Payment successful, new wallet balance: " + (balanceData + newBalance));
            } else {
                System.out.println("Login failed. Invalid credentials.");
            }
        } catch (AuthenticationException e) {
            System.out.println("Customer ID not correct: " + e.getMessage());
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
            System.out.println("5. Change City");
            System.out.println("6. Change Pin Code");
            System.out.println("7. Change Payment Details");
            System.out.println("8. Back to main menu");
            System.out.print("Enter your choice: ");
            int editChoice = 0;
            try {
                editChoice = in.nextInt();
                in.nextLine(); // Consume newline
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                in.nextLine();
                continue;
            }

            if (editChoice == 8) {
                continueEditing = false;
                break;
            }

            if(editChoice == 7){
                System.out.print("Enter new payment type (1 for Card, 2 for UPI): ");
                String paymentType = in.nextLine();
                while(!paymentType.equals("1") && !paymentType.equals("2")) {
                    System.out.println("Invalid payment type. Please enter valid option");
                    System.out.print("Enter new payment type (1 for Card, 2 for UPI): ");
                    paymentType = in.nextLine();
                }

                System.out.print("Enter new payment info (Card number or UPI ID): ");
                String paymentInfo = in.nextLine();

                try {
                    // Call the service method to update the customer's details based on their choice.
                    customerService.editPaymentDetails(loggedInCustomerId, Integer.parseInt(paymentType), paymentInfo);
                } catch (IndexOutOfBoundsException | MismatchinputException e) {
                    System.out.println("Error: " + e.getMessage());
                } catch (Exception e) {
                    System.out.println("An unexpected error occurred: " + e.getMessage());
                }
            }
            else{
                System.out.print("Enter new value: ");
                String newValue = in.nextLine();

                try {
                    // Call the service method to update the customer's details based on their choice.
                    customerService.editCustomerDetails(loggedInCustomerId, editChoice, newValue);
                } catch (IndexOutOfBoundsException | MismatchinputException e) {
                    System.out.println("Error: " + e.getMessage());
                } catch (Exception e) {
                    System.out.println("An unexpected error occurred: " + e.getMessage());
                }
            }
        }
    }
}