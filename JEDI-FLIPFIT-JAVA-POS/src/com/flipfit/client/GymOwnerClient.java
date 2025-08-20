package com.flipfit.client;

import com.flipfit.business.GymOwnerService;
import java.util.Scanner;

public class GymOwnerClient {

    private GymOwnerService gymOwnerService;
    private Scanner in;

    public GymOwnerClient() {
        this.gymOwnerService = new GymOwnerService();
        this.in = new Scanner(System.in);
    }

    public void gymOwnerPage() {
        while (true) {
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
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter a number.");
                in.next();
                continue;
            }

            switch (choice) {
                case 1:
//                    addCentre();
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

//    private void addCentre() {
//        System.out.println("Adding a new gym centre...");
//        // You would prompt for gym details here and then call the service layer
//        // For example: gymOwnerService.addCentre(new GymCentre(...));
//        gymOwnerService.addCentre();
//    }

    private void viewGymDetails() {
        System.out.println("Viewing your gym details...");
        gymOwnerService.viewGymDetails();
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
        System.out.println("Editing your personal details...");
//        gymOwnerService.editDetails();
    }
}