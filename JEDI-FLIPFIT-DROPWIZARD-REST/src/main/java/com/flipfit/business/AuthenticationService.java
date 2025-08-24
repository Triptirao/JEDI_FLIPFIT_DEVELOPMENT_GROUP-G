package com.flipfit.business;

import com.flipfit.bean.Customer;
import com.flipfit.bean.GymOwner;
import com.flipfit.bean.User;
import com.flipfit.dao.CustomerDAO;
import com.flipfit.dao.GymOwnerDAO;
import com.flipfit.dao.UserDAO;
import com.flipfit.exception.AuthenticationException;
import com.flipfit.exception.DuplicateEntryException;
import com.flipfit.exception.MismatchinputException;
import com.flipfit.exception.MissingValueException;

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
     * @return A User object if authentication is successful.
     * @throws AuthenticationException if the user's credentials are invalid.
     */
    @Override
    public User login(String email, String password) throws AuthenticationException {
        Optional<User> userOptional = userDao.getUserByEmailAndPassword(email, password);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (customerDao.getCustomerById(user.getUserId()).isPresent()) {
                user.setRole("CUSTOMER");
            } else if (gymOwnerDao.getGymOwnerById(user.getUserId()).isPresent()) {
                user.setRole("OWNER");
            } else {
                user.setRole("ADMIN");
            }
            return user;
        }
        throw new AuthenticationException("Invalid email or password.");
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
     * @throws DuplicateEntryException if a user with the same email already exists.
     * @throws MismatchinputException if there is an issue with the provided input.
     */
    @Override
    public void registerCustomer(String fullName, String email, String password, long userPhone, String city, int pinCode, int paymentType, String paymentInfo) throws DuplicateEntryException, MismatchinputException, MissingValueException {

        if (fullName == null || fullName.trim().isEmpty() || password == null || password.trim().isEmpty() || email == null || email.trim().isEmpty()) {
            throw new MismatchinputException("Full name, email, and password cannot be empty.");
        }

        User newUser = new User(fullName, email, password, userPhone, city, pinCode);
        newUser.setRole("CUSTOMER");
        int userId = userDao.addUser(newUser);

        if (userId != -1) {
            Customer newCustomer = new Customer(userId, paymentType, paymentInfo, 0);
            customerDao.addCustomer(newCustomer);
        } else {
            throw new MismatchinputException("User registration failed due to an internal error.");
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
     * @throws DuplicateEntryException if a user with the same email already exists.
     * @throws MismatchinputException if there is an issue with the provided input.
     */
    @Override
    public void registerGymOwner(String fullName, String email, String password, long userPhone, String city, int pinCode, String aadhaar, String pan, String gst) throws DuplicateEntryException, MismatchinputException {


        if (fullName == null || fullName.trim().isEmpty() || password == null || password.trim().isEmpty() || email == null || email.trim().isEmpty()) {
            throw new MismatchinputException("Full name, email, and password cannot be empty.");
        }

        User newUser = new User(fullName, email, password, userPhone, city, pinCode);
        newUser.setRole("OWNER");
        int userId = userDao.addUser(newUser);

        if (userId != -1) {
            GymOwner newOwner = new GymOwner(userId, pan, aadhaar, gst, false);
            gymOwnerDao.addGymOwner(newOwner);
        } else {
            throw new MismatchinputException("Gym owner registration failed due to an internal error.");
        }
    }
}
