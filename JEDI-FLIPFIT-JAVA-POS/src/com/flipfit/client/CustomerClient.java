package com.flipfit.client;

import com.flipfit.bean.Booking;
import com.flipfit.bean.GymCentre;
import com.flipfit.business.CustomerService;
import com.flipfit.dao.*;

import java.util.List;
import java.util.Scanner;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class CustomerClient {

    private GymOwnerDAO gymOwnerDao;
    private final CustomerService customerService;
    private Scanner in;
    private int loggedInCustomerId; // To track the logged-in user


    // The constructor now receives DAOs as dependencies
    public CustomerClient(CustomerDAO customerDAO, UserDAO userDao, int loggedInCustomerId) {
        this.customerService = new CustomerService(userDao,customerDAO, this.gymOwnerDao);
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
        System.out.print("Enter Gym ID: ");
        int gymId = in.nextInt();
        System.out.print("Enter Slot ID: ");
        int slotId = in.nextInt();
        in.nextLine(); // Consume newline
        System.out.print("Enter Booking Date (YYYY-MM-DD): ");
        String dateStr = in.nextLine();

        // Parse the date from the user input
        LocalDate bookingDate = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        // Use the existing service object to book the slot
        customerService.bookSlot(loggedInCustomerId, gymId, slotId, bookingDate);
    }
    private void viewBookedSlots() {
        System.out.println("Viewing your booked slots...");

        // Assuming `loggedInCustomerId` is a String and `customerService` is a valid instance.
        // The `viewBookedSlots` method on customerService should return List<Booking>
        // to be consistent with this code block.
        List<Booking> bookings = customerService.viewBookedSlots(loggedInCustomerId);

        if (bookings.isEmpty()) {
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
        List<GymCentre> centers = customerService.viewCenters();
        if (centers.isEmpty()) {
            System.out.println("No gym centers found.");
        } else {
            System.out.println("Gym Centers:");
            // Iterate through the list of GymCentre objects
            for (GymCentre center : centers) {
                // Access properties using getter methods
                System.out.println("ID: " + center.getCentreId() +
                        ", Name: " + center.getCentreName() +
                        ", City: " + center.getCity());
            }
        }
    }
    private void makePayments() {
        System.out.println("Initiating payment process...");
        System.out.print("Enter payment type (1 for Credit Card, 2 for Debit Card, etc.): ");
        int paymentType = in.nextInt();
        in.nextLine(); // Consume newline
        System.out.print("Enter account number: ");
        String paymentInfo = in.nextLine();

        // Pass the logged-in customer's ID to the service method
        customerService.makePayments(loggedInCustomerId, paymentType, paymentInfo);
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
