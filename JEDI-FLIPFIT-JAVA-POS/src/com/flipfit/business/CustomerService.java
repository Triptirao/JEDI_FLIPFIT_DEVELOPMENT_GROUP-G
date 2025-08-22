package com.flipfit.business;

import com.flipfit.bean.Booking;
import com.flipfit.dao.CustomerDAO;
import com.flipfit.dao.GymOwnerDAO;
import com.flipfit.dao.UserDAO;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * The CustomerService class provides business logic for customer-related operations.
 * It allows customers to view and manage their bookings, browse gym centers,
 * and update their personal details.
 *
 * @author
 */
public class CustomerService implements customerInterface {

    private CustomerDAO customerDao;
    private GymOwnerDAO gymOwnerDao;
    private UserDAO userDao;

    /**
     * Parameterized constructor for CustomerService.
     *
     * @param customerDao The Data Access Object for customer operations.
     * @param userDao The Data Access Object for general user operations.
     * @param gymOwnerDao The Data Access Object for gym owner operations.
     */
    public CustomerService(CustomerDAO customerDao, UserDAO userDao, GymOwnerDAO gymOwnerDao) {
        this.customerDao = customerDao;
        this.userDao = userDao;
        this.gymOwnerDao = gymOwnerDao;
    }

    /**
     * Retrieves and displays a list of all booked slots for a specific customer.
     *
     * @param customerId The unique ID of the customer whose bookings are to be viewed.
     * @return A list of Booking objects representing the customer's booked slots, or null if an error occurs.
     */
    public List<Booking> viewBookedSlots(String customerId) {
        System.out.println("Fetching your booked slots...");
        try {
            int customerIdInt = Integer.parseInt(customerId);
            return customerDao.getBookingsByCustomerId(customerIdInt);
        } catch (NumberFormatException e) {
            System.err.println("Error: The customer ID must be a valid number.");
            return null;
        }
    }

    /**
     * Fetches and displays a list of all available and approved gym centers.
     *
     * @return A list of String arrays, where each array contains the details of a gym center.
     */
    public List<String[]> viewCenters() {
        System.out.println("Fetching all available gym centers...");
        return gymOwnerDao.getApprovedGyms();
    }

    /**
     * Books a new slot for a customer at a specific gym center.
     *
     * @param bookingId The unique ID for the new booking.
     * @param customerId The ID of the customer making the booking.
     * @param slotId The ID of the slot being booked.
     * @param centreId The ID of the gym center where the slot is located.
     */
    public void bookSlot(int bookingId, int customerId, int slotId, int centreId) {
        System.out.println("Booking your slot...");
        Booking newBooking = new Booking(
                bookingId,
                customerId,
                centreId,
                slotId,
                "BOOKED",
                LocalDate.now(),
                LocalTime.now()
        );
        customerDao.bookSlot(newBooking);
        System.out.println("Slot booked successfully!");
    }

    /**
     * Processes a customer's payment information.
     *
     * @param paymentType The type of payment being made (e.g., credit card, cash).
     * @param paymentInfo The details associated with the payment method (e.g., account number).
     */
    public void makePayments(int paymentType, String paymentInfo) {
        System.out.println("Processing payment of type: " + paymentType + " with account: " + paymentInfo);
    }

    /**
     * Updates a specific detail for a customer.
     *
     * @param customerId The ID of the customer whose details are to be updated.
     * @param choice An integer representing the detail to be updated (e.g., 1 for name, 2 for phone number).
     * @param newValue The new value for the selected detail.
     */
    public void editCustomerDetails(String customerId, int choice, String newValue) {
        userDao.updateUserDetails(customerId, choice, newValue);
        System.out.println("Customer details updated successfully.");
    }
}
