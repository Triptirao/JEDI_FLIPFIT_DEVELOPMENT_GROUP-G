package com.flipfit.business;

import com.flipfit.bean.User;

/**
 * The AuthenticationInterface defines the contract for all authentication and
 * registration operations. Any class that implements this interface must provide
 * the functionality to log in users and register new customers or gym owners.
 *
 * @author
 */
public interface authenticationInterface {

    /**
     * Authenticates a user based on their email and password.
     *
     * @param email The user's email address.
     * @param password The user's password.
     * @return A User object if authentication is successful, otherwise null.
     */
    User login(String email, String password);

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
    void registerCustomer(String fullName, String email, String password, long userPhone, String city, int pinCode, int paymentType, String paymentInfo);

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
    void registerGymOwner(String fullName, String email, String password, long userPhone, String city, int pinCode, String aadhaar, String pan, String gst);
}
