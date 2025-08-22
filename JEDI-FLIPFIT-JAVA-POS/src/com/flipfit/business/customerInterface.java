package com.flipfit.business;

import com.flipfit.bean.Booking;
import com.flipfit.bean.GymCentre;
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
     * Processes a customer's payment and updates their payment details.
     *
     * @param customerId The ID of the customer making the payment.
     * @param paymentType The type of payment being made.
     * @param paymentInfo The details associated with the payment method.
     */
    void makePayments(int customerId, int paymentType, String paymentInfo);

    /**
     * Updates a specific detail for a customer.
     *
     * @param userId The ID of the user (customer) whose details are to be updated.
     * @param choice An integer representing the detail to be updated (e.g., 1 for name, 2 for email).
     * @param newValue The new value for the selected detail.
     */
    void editCustomerDetails(int userId, int choice, String newValue);
}
