package com.flipfit.business;

import com.flipfit.dao.UserDAO;
import com.flipfit.dao.CustomerDAO;
import com.flipfit.dao.GymOwnerDAO;
import com.flipfit.bean.User;
import com.flipfit.bean.Customer;
import com.flipfit.bean.GymOwner;

import java.util.Optional;

/**
 * The AuthenticationService class handles all user authentication and registration
 * processes for customers and gym owners.
 *
 * @author
 */
public class AuthenticationService implements authenticationInterface {

    private final UserDAO userDao;
    private final CustomerDAO customerDao;
    private final GymOwnerDAO gymOwnerDao;

    public AuthenticationService(UserDAO userDao, CustomerDAO customerDao, GymOwnerDAO gymOwnerDao) {
        this.userDao = userDao;
        this.customerDao = customerDao;
        this.gymOwnerDao = gymOwnerDao;
    }

    /**
     * Authenticates a user based on their email and password.
     *
     * @param email The user's email address.
     * @param password The user's password.
     * @return A User object if authentication is successful, otherwise null.
     */
    @Override
    public User login(String email, String password) {
        // Authenticate user by checking against the database
        Optional<User> userOptional = userDao.getUserByEmailAndPassword(email, password);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            // Check if the user is a customer or a gym owner to determine the role
            if (customerDao.getCustomerById(user.getUserId()).isPresent()) {
                user.setRole("CUSTOMER");
            } else if (gymOwnerDao.getGymOwnerById(user.getUserId()).isPresent()) {
                user.setRole("OWNER");
            } else {
                // If the user ID exists in the User table but not in Customer or GymOwner,
                // you might assume they are an Admin.
                user.setRole("ADMIN");
            }
            return user;
        }
        return null;
    }

    /**
     * Registers a new customer in the system.
     *
     * @param fullName The customer's full name.
     * @param email The customer's email address.
     * @param password The customer's password.
     * @param userPhone The customer's phone number.
     * @param city The customer's city.
     * @param pinCode The customer's pincode.
     * @param paymentType The customer's payment type.
     * @param paymentInfo The customer's payment information.
     */
    @Override
    public void registerCustomer(String fullName, String email, String password, long userPhone, String city, int pinCode, int paymentType, String paymentInfo) {
        // Create a new User object and persist it to the User table
        User newUser = new User(fullName, email, password, userPhone, city, pinCode);
        int userId = userDao.addUser(newUser);

        if (userId != -1) {
            // Create a new Customer object and link it to the new user ID
            Customer newCustomer = new Customer(userId, paymentType, paymentInfo);
            customerDao.addCustomer(newCustomer);
            System.out.println("Customer registration received for " + fullName);
        } else {
            System.out.println("User registration failed.");
        }
    }

    /**
     * Registers a new gym owner in the system.
     *
     * @param fullName The gym owner's full name.
     * @param email The gym owner's email address.
     * @param password The gym owner's password.
     * @param userPhone The gym owner's phone number.
     * @param city The gym owner's city.
     * @param pinCode The gym owner's pincode.
     * @param aadhaar The gym owner's Aadhaar number.
     * @param pan The gym owner's PAN number.
     * @param gst The gym owner's GST number.
     */
    @Override
    public void registerGymOwner(String fullName, String email, String password, long userPhone, String city, int pinCode, String aadhaar,String pan, String gst) {
        // Create a new User object and persist it to the User table
        User newUser = new User(fullName, email, password, userPhone, city, pinCode);
        int userId = userDao.addUser(newUser);

        if (userId != -1) {
            // Create a new GymOwner object and link it to the new user ID
            GymOwner newOwner = new GymOwner(userId, pan, aadhaar, gst,false);
            gymOwnerDao.addGymOwner(newOwner);
            System.out.println("Gym owner registration received for " + fullName);
        } else {
            System.out.println("User registration failed.");
        }
    }
}
