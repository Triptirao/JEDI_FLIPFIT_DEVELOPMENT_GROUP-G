package com.flipfit.business;

import com.flipfit.dao.CustomerDAO;
import com.flipfit.dao.GymOwnerDAO;
import com.flipfit.dao.UserDAO;
import com.flipfit.bean.GymCentre;
import com.flipfit.bean.User;
import com.flipfit.bean.GymOwner;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * The GymOwnerService class provides the business logic for all operations
 * a gym owner can perform, such as managing gym centers, viewing customer lists,
 * and editing their personal details.
 *
 * @author
 */
public class GymOwnerService implements gymOwnerInterface {

    private static final Scanner in = new Scanner(System.in);
    private GymOwnerDAO gymOwnerDao;
    private UserDAO userDao;
    private CustomerDAO customerDao;

    public GymOwnerService(UserDAO userDao, CustomerDAO customerDao,GymOwnerDAO gymOwnerDao) {
        this.gymOwnerDao = gymOwnerDao;
        this.userDao = userDao;
        this.customerDao = customerDao;
    }

    /**
     * Adds a new gym center to the system.
     * @param gymData A GymCentre object containing the details of the new gym center.
     */
    @Override
    public void addCentre(GymCentre gymData) {
        gymOwnerDao.addGym(gymData);
        System.out.println("Adding new gym centre: " + gymData.getCentreName());
    }

    /**
     * Retrieves and displays the details of all gym centers owned by a specific gym owner.
     * @param ownerId The unique ID of the gym owner.
     */
    @Override
    public void viewGymDetails(int ownerId) {
        System.out.println("Fetching details for all your gym centres...");
        List<GymCentre> gyms = gymOwnerDao.getGymsByOwnerId(ownerId);

        System.out.println("------------------------------------------------------------------");
        System.out.printf("%-15s %-20s %-20s %n", "Gym ID", "Name", "Location");
        System.out.println("------------------------------------------------------------------");
        if (gyms.isEmpty()) {
            System.out.println("No gym centers found.");
        } else {
            for (GymCentre gym : gyms) {
                System.out.printf("%-15s %-20s %-20s %n", gym.getCentreId(), gym.getCentreName(), gym.getCity() + ", " + gym.getState());
            }
        }
        System.out.println("------------------------------------------------------------------");
    }

    /**
     * Retrieves and displays the list of customers for the gym owner's centers.
     */
    @Override
    public void viewCustomers() {
        System.out.println("Fetching customer list...");
        List<User> customers = userDao.getAllCustomers();

        if (customers.isEmpty()) {
            System.out.println("No customers found.");
            return;
        }

        System.out.println("----------------------------------------");
        System.out.printf("%-10s %-20s %n", "ID", "Name");
        System.out.println("----------------------------------------");
        for (User customer : customers) {
            System.out.printf("%-10d %-20s %n", customer.getUserId(), customer.getFullName());
        }
        System.out.println("----------------------------------------");
    }

    /**
     * Retrieves and displays the payment history for the gym owner.
     */
    @Override
    public void viewPayments() {
        System.out.println("Fetching payment history...");
        System.out.println("No payments found.");
    }

    /**
     * Updates a specific detail for the gym owner.
     * @param ownerId The ID of the gym owner whose details are to be updated.
     * @param choice An integer representing the detail to be updated (e.g., 1 for name, 2 for email).
     * @param newValue The new value for the selected detail.
     */
    @Override
    public void editGymOwnerDetails(int ownerId, int choice, String newValue) {
        // Fetch the existing user and gym owner data from the database
        Optional<User> userOptional = userDao.getUserById(ownerId);
        Optional<GymOwner> gymOwnerOptional = gymOwnerDao.getGymOwnerById(ownerId);

        if (!userOptional.isPresent() || !gymOwnerOptional.isPresent()) {
            System.out.println("Error: User or Gym Owner not found.");
            return;
        }

        User user = userOptional.get();
        GymOwner gymOwner = gymOwnerOptional.get();

        // Update the correct field based on user choice
        switch (choice) {
            case 1: user.setFullName(newValue); break;
            case 2: user.setEmail(newValue); break;
            case 3: user.setPassword(newValue); break;
            case 4: user.setUserPhone(Long.parseLong(newValue)); break;
            case 5: user.setCity(newValue); break;
            case 6: user.setPinCode(Integer.parseInt(newValue)); break;
            case 7: gymOwner.setPan(newValue); break;
            case 8: gymOwner.setAadhaar(newValue); break;
            case 9: gymOwner.setGst(newValue); break;
            default: System.out.println("Invalid choice for update."); return;
        }

        // Persist the changes to the database by calling the DAOs
        userDao.updateUser(user);
        gymOwnerDao.updateGymOwnerDetails(gymOwner);
        System.out.println("Owner details updated successfully.");
    }

    /**
     * Displays the main menu for a logged-in gym owner and handles user input.
     * @param loggedInOwnerId The ID of the currently logged-in gym owner.
     */
    @Override
    public void displayGymOwnerMenu(int loggedInOwnerId) {
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
                        System.out.print("Enter gym name: ");
                        String name = in.nextLine();
                        System.out.print("Enter gym capacity: ");
                        int capacity = in.nextInt();
                        in.nextLine();
                        System.out.print("Enter gym city: ");
                        String city = in.nextLine();
                        System.out.print("Enter gym state: ");
                        String state = in.nextLine();
                        System.out.print("Enter gym pincode: ");
                        String pincode = in.nextLine();

                        GymCentre newGym = new GymCentre(0, loggedInOwnerId, name, null, capacity, false, city, state, pincode, null);
                        addCentre(newGym);

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
                        in.nextLine();
                        if (editChoice == 10) {
                            continueEditing = false;
                            break;
                        }

                        System.out.print("Enter new value: ");
                        String newValue = in.nextLine();
                        editGymOwnerDetails(loggedInOwnerId, editChoice, newValue);
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
