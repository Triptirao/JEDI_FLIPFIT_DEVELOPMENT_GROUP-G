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

public class GymOwnerClient {

    private final GymOwnerService gymOwnerService;
    private final Scanner in;
    private final UserDAO userDao;
    private final GymOwnerDAO gymOwnerDao;
    private final int loggedInOwnerId;

    public GymOwnerClient(UserDAO userDao, CustomerDAO customerDao,GymOwnerDAO gymOwnerDAO, int loggedInOwnerId) {
        this.gymOwnerDao = gymOwnerDAO;
        this.userDao = userDao;
        this.loggedInOwnerId = loggedInOwnerId;
        this.gymOwnerService = new GymOwnerService(userDao, customerDao, gymOwnerDAO);
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
                in.nextLine();
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
        Optional<GymOwner> ownerData = gymOwnerDao.getGymOwnerById(loggedInOwnerId);
        if (ownerData.isPresent()) {
            GymOwner owner = ownerData.get();
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

            gymOwnerService.editGymOwnerDetails(loggedInOwnerId, editChoice, newValue);
        }
    }
}