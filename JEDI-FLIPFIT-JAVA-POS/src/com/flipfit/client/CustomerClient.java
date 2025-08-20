package com.flipfit.client;

import com.flipfit.business.CustomerService;
import java.util.Scanner;

public class CustomerClient {

    private CustomerService customerService;
    private Scanner in;

    public CustomerClient() {
        this.customerService = new CustomerService();
        this.in = new Scanner(System.in);
    }

    public void customerPage() {
        while (true) {
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
                    return;
                default:
                    System.out.println("Invalid number. Please try again.");
            }
        }
    }

    private void viewBookedSlots() {
        System.out.println("Viewing your booked slots...");
//        customerService.viewBookedSlots();
    }

    private void viewCenters() {
        System.out.println("Viewing all available gym centers...");
        customerService.viewCenters();
    }

    private void makePayments() {
        System.out.println("Initiating payment process...");
        System.out.print("Enter payment type (1 for Credit Card, 2 for Debit Card, etc.): ");
        int paymentType = in.nextInt();
        in.nextLine(); // Consume newline
        System.out.print("Enter payment info: ");
        String paymentInfo = in.nextLine();

        // This would typically involve a secure payment gateway integration
        customerService.makePayments(paymentType, paymentInfo);
    }

    private void editDetails() {
        System.out.println("Editing your profile details...");
        customerService.editDetails();
    }
}