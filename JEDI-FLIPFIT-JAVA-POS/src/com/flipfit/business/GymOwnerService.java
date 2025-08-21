package com.flipfit.business;

import com.flipfit.dao.CustomerDAO;
import com.flipfit.dao.GymOwnerDAO;
import com.flipfit.dao.UserDAO;

import java.util.List;
import java.util.Scanner;

public class GymOwnerService implements gymOwnerInterface {

    private static final Scanner in = new Scanner(System.in);
    private GymOwnerDAO gymOwnerDao;
    private CustomerDAO customerDao;
    private UserDAO userDao;

    public GymOwnerService(GymOwnerDAO gymOwnerDao, UserDAO userDao, CustomerDAO customerDao) {
        this.gymOwnerDao = gymOwnerDao;
        this.userDao = userDao;
        this.customerDao = customerDao;
    }
    public void addCentre(String[] gymData) {
        gymOwnerDao.addGym(gymData);
        System.out.println("Adding new gym centre: " + gymData[2]);
    }

    public void viewGymDetails(String ownerId) {
        System.out.println("Fetching details for all your gym centres...");
        List<String[]> gyms = gymOwnerDao.getGymsByOwnerId(ownerId);

        System.out.println("------------------------------------------------------------------");
        System.out.printf("%-15s %-20s %-20s %n", "Gym ID", "Name", "Location");
        System.out.println("------------------------------------------------------------------");
        if (gyms.isEmpty()) {
            System.out.println("No gym centers found.");
        } else {
            for (String[] gym : gyms) {
                System.out.printf("%-15s %-20s %-20s %n", gym[0], gym[2], gym[5] + ", " + gym[6]);
            }
        }
        System.out.println("------------------------------------------------------------------");
    }

    public void viewCustomers() {
        System.out.println("Fetching customer list for your gym centres...");
        // This would call a method in a CustomerDAO
        System.out.println("No customers found.");
    }

    public void viewPayments() {
        System.out.println("Fetching payment history...");
        System.out.println("No payments found.");
    }

    public void editGymOwnerDetails(String ownerId, int choice, String newValue) {
        gymOwnerDao.updateGymOwnerDetails(ownerId, choice, newValue);
        System.out.println("Owner details updated successfully.");
    }

    public void displayGymOwnerMenu(String loggedInOwnerId) {
        boolean exitOwnerMenu = false;
        while (!exitOwnerMenu) {
            System.out.println("\n*** Welcome, Gym Owner! ***");
            System.out.println("1. Add a new Gym Centre");
            System.out.println("2. View My Gym Details");
            System.out.println("3. View My Customer List");
            System.out.println("4. View Payments");
            System.out.println("5. Edit My Details");
            System.out.println("6. Exit");
            System.out.print("Enter your choice: ");

            int choice = in.nextInt();
            in.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    boolean continueAdding = true;
                    while (continueAdding) {
                        System.out.println("\n--- Add a New Gym Centre ---");
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
                        addCentre(newGymData);

                        System.out.println("Gym Centre with name " + name + " and location " + city + ", " + state + " added. Do you want to add another? (yes/no)");
                        String response = in.nextLine();
                        if (response.equalsIgnoreCase("no")) {
                            continueAdding = false;
                        }
                    }
                    break;
                case 2:
                    viewGymDetails(loggedInOwnerId);
                    break;
                case 3:
                    viewCustomers();
                    break;
                case 4:
                    viewPayments();
                    break;
                case 5:
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
                                editGymOwnerDetails(loggedInOwnerId, 1, newValue);
                                break;
                            case 2:
                                System.out.print("Enter new email: ");
                                newValue = in.nextLine();
                                editGymOwnerDetails(loggedInOwnerId, 2, newValue);
                                break;
                            case 3:
                                System.out.print("Enter new password: ");
                                newValue = in.nextLine();
                                editGymOwnerDetails(loggedInOwnerId, 3, newValue);
                                break;
                            case 4:
                                System.out.print("Enter new phone number: ");
                                newValue = in.nextLine();
                                editGymOwnerDetails(loggedInOwnerId, 4, newValue);
                                break;
                            case 5:
                                System.out.print("Enter new city: ");
                                newValue = in.nextLine();
                                editGymOwnerDetails(loggedInOwnerId, 5, newValue);
                                break;
                            case 6:
                                System.out.print("Enter new pincode: ");
                                newValue = in.nextLine();
                                editGymOwnerDetails(loggedInOwnerId, 6, newValue);
                                break;
                            case 7:
                                System.out.print("Enter new PAN: ");
                                newValue = in.nextLine();
                                editGymOwnerDetails(loggedInOwnerId, 7, newValue);
                                break;
                            case 8:
                                System.out.print("Enter new Aadhaar: ");
                                newValue = in.nextLine();
                                editGymOwnerDetails(loggedInOwnerId, 8, newValue);
                                break;
                            case 9:
                                System.out.print("Enter new GST: ");
                                newValue = in.nextLine();
                                editGymOwnerDetails(loggedInOwnerId, 9, newValue);
                                break;
                            case 10:
                                continueEditing = false;
                                break;
                            default:
                                System.out.println("Invalid choice. Please try again.");
                        }
                    }
                    break;
                case 6:
                    exitOwnerMenu = true;
                    System.out.println("Exiting Gym Owner menu.");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
}
