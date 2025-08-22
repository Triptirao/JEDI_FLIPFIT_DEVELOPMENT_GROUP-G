package com.flipfit.business;

import com.flipfit.bean.Customer;
import com.flipfit.bean.User;
import com.flipfit.dao.UserDAO;

/**
 * The AuthenticationService class handles all user authentication and registration
 * processes for customers and gym owners.
 *
 * @author
 */
public class AuthenticationService implements authenticationInterface {

    private UserDAO userDao;

    /**
     * Default constructor for AuthenticationService. Initializes the UserDAO.
     */
    public AuthenticationService() {
        this.userDao = new UserDAO();
    }

    /**
     * Parameterized constructor for AuthenticationService.
     *
     * @param userDao The Data Access Object for user-related operations.
     */
    public AuthenticationService(UserDAO userDao) {
        this.userDao = userDao;
    }

    /**
     * Authenticates a user by checking their email and password against the
     * existing user data.
     *
     * @param email The user's email address.
     * @param password The user's password.
     * @return A String array containing the user's details if authentication is successful, otherwise null.
     */
    public String[] login(String email, String password) {
        // Authenticate user by checking against all users in the DAO
        for (String[] user : userDao.getAllUsers()) {
            if (user[3].equals(email) && user[4].equals(password)) {
                return user;
            }
        }
        return null;
    }

    /**
     * Registers a new customer with their details. It first adds the user to the
     * user database and then adds the customer-specific details.
     *
     * @param name The customer's full name.
     * @param email The customer's email address.
     * @param password The customer's password.
     * @param phone The customer's phone number.
     * @param city The customer's city.
     * @param pincode The customer's pincode.
     * @param paymentType The customer's payment type (e.g., credit card, debit card).
     * @param paymentInfo The customer's payment information.
     */
    public void registerCustomer(String name, String email, String password, long phone, String city, int pincode, int paymentType, String paymentInfo) {
        // Here you would generate a unique ID for the new customer
        User user = new User(1, "customer", 0, name, email, password, phone, city, pincode);
        int userId = userDao.addUser(user);
        if(userId == -1) {
            System.out.println("Error in registering customer");
        }
        else {
            Customer customer = new Customer(1, "customer", userId, name, email, password, phone, city, pincode, paymentType, paymentInfo);
            if(userDao.addCustomer(customer) == true){
                System.out.println("Customer added successfully");
            }
            else {
                System.out.println("Error in registering customer");
            }
        }
    }

    /**
     * Registers a new gym owner. Note: The implementation is currently commented out.
     *
     * @param name The gym owner's full name.
     * @param email The gym owner's email address.
     * @param password The gym owner's password.
     * @param phone The gym owner's phone number.
     * @param aadhaar The gym owner's Aadhaar number.
     * @param pan The gym owner's PAN number.
     * @param gst The gym owner's GST number.
     */
    public void registerGymOwner(String name, String email, String password, long phone, String aadhaar, String pan, String gst) {
        // Here you would generate a unique ID for the new gym owner
//        String newId = String.valueOf(userDao.getAllUsers().size() + 1);
//        String[] newOwner = {"OWNER", newId, name, email, password, phone, "N/A", "N/A", pan, aadhaar, gst, "false"};
//        userDao.getAllUsers().add(newOwner);
//        System.out.println("Gym owner registration received for " + name);
    }
}
