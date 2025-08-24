package com.flipfit.business;

import com.flipfit.bean.Booking;
import com.flipfit.bean.Customer;
import com.flipfit.bean.GymCentre;
import com.flipfit.bean.User;
import com.flipfit.exception.AuthenticationException;
import com.flipfit.exception.MismatchinputException;
import com.flipfit.exception.MissingValueException;

import java.time.LocalDate;
import java.util.List;

/**
 * The CustomerInterface defines the contract for all customer-related operations.
 * Any class that implements this interface must provide the functionality
 * for viewing bookings, browsing centers, booking slots, making payments,
 * and editing personal details.
 *
 * @author
 */
public interface customerInterface {

    /**
     * Retrieves a list of all booked slots for a specific customer.
     *
     * @param customerId The unique ID of the customer whose bookings are to be viewed.
     * @return A list of Booking objects representing the customer's booked slots.
     */
    List<Booking> viewBookedSlots(int customerId);

    /**
     * Fetches a list of all available and approved gym centers.
     *
     * @return A list of GymCentre objects representing the available gyms.
     */
    List<GymCentre> viewCenters();

    /**
     * Books a new slot for a customer at a specific gym center.
     *
     * @param customerId The ID of the customer making the booking.
     * @param gymId The ID of the gym center where the slot is located.
     * @param slotId The ID of the slot being booked.
     * @param bookingDate The date for which the slot is being booked.
     */
    void bookSlot(int customerId, int gymId, int slotId, LocalDate bookingDate);

    /**
     * Processes a customer's payment and updates their wallet balance.
     *
     * @param customerId The ID of the customer making the payment.
     * @param balance The balance to be added to wallet.
     */
    void makePayments(int customerId, int balance);

    /**
     * Retrieves a customer's balance.
     *
     * @param customerId The customer's user ID.
     * @return An integer representing the balance.
     * @throws AuthenticationException if the user ID is invalid.
     */
    Integer retrieveBalance(int customerId) throws AuthenticationException;

    /**
     * Updates user and customer details.
     *
     * @param userId The ID of the user (customer) whose details are to be updated.
     * @param updatedUser An object containing the new User details.
     * @param updatedCustomer An object containing the new Customer details.
     */
    void editCustomerDetails(int userId, User updatedUser, Customer updatedCustomer);

    /**
     * Updates a customer's payment-specific details.
     *
     * @param userId The ID of the user (customer).
     * @param paymentType The new payment type.
     * @param paymentInfo The new payment information.
     */
    void editPaymentDetails(int userId, int paymentType, String paymentInfo);
}