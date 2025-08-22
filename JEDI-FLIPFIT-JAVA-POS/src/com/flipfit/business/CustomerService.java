package com.flipfit.business;

import com.flipfit.bean.Booking;
import com.flipfit.bean.GymCentre;
import com.flipfit.dao.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * The CustomerService class provides the business logic for customer-related operations.
 * It allows customers to view and manage their bookings, browse gym centers,
 * and update their personal details.
 *
 * @author
 */
public class CustomerService implements customerInterface {

    private CustomerDAO customerDao;
    private UserDAO userDao;
    private GymOwnerDAO gymOwnerDao;

    public CustomerService(UserDAO userDao, CustomerDAO customerDao,GymOwnerDAO gymOwnerDao) {
        this.customerDao = customerDao;
        this.userDao = userDao;
        this.gymOwnerDao = gymOwnerDao;
    }

    /**
     * Retrieves a list of all booked slots for a specific customer.
     *
     * @param customerId The unique ID of the customer whose bookings are to be viewed.
     * @return A list of Booking objects representing the customer's booked slots.
     */
    @Override
    public List<Booking> viewBookedSlots(int customerId) {
        System.out.println("Fetching your booked slots...");
        // Call the new database-based method from BookingDAO
        return customerDao.getBookingsByCustomerId(customerId);
    }

    /**
     * Fetches a list of all available and approved gym centers.
     *
     * @return A list of GymCentre objects representing the available gyms.
     */
    @Override
    public List<GymCentre> viewCenters() {
        System.out.println("Fetching all available gym centers...");
        // Call the new database-based method from GymCentreDAO
        return customerDao.getApprovedGyms();
    }

    /**
     * Books a new slot for a customer at a specific gym center.
     *
     * @param customerId The ID of the customer making the booking.
     * @param gymId The ID of the gym center where the slot is located.
     * @param slotId The ID of the slot being booked.
     * @param bookingDate The date for which the slot is being booked.
     */
    @Override
    public void bookSlot(int customerId, int gymId, int slotId, LocalDate bookingDate) {
        System.out.println("Booking your slot...");
        System.out.println("GymId: " + gymId + ", SlotId: " + slotId + ", BookingDate: " + bookingDate + "customerid: " + customerId);
        // Create a new Booking object with the provided details
        Booking newBooking = new Booking(
                customerId,
                gymId,
                slotId,
                "BOOKED", // Default status
                bookingDate, // Use the provided date
                LocalTime.now()
        );

        // Call the database-based method in the DAO
        customerDao.bookSlot(newBooking);

        System.out.println("Slot booked successfully!");
    }

    /**
     * Processes a customer's payment and updates their payment details.
     *
     * @param customerId The ID of the customer making the payment.
     * @param paymentType The type of payment being made.
     * @param paymentInfo The details associated with the payment method.
     */
    @Override
    public void makePayments(int customerId, int paymentType, String paymentInfo) {
        System.out.println("Processing payment of type: " + paymentType + " with account: " + paymentInfo);
        // Fetch the customer to update their payment details
        customerDao.getCustomerById(customerId).ifPresent(customer -> {
            customer.setPaymentType(paymentType);
            customer.setPaymentInfo(paymentInfo);
            customerDao.updateCustomer(customer);
        });
    }

    /**
     * Updates a specific detail for a customer.
     *
     * @param userId The ID of the user (customer) whose details are to be updated.
     * @param choice An integer representing the detail to be updated (e.g., 1 for name, 2 for email).
     * @param newValue The new value for the selected detail.
     */
    @Override
    public void editCustomerDetails(int userId, int choice, String newValue) {
        userDao.getUserById(userId).ifPresent(user -> {
            switch (choice) {
                case 1: // Name
                    user.setFullName(newValue);
                    break;
                case 2: // Email
                    user.setEmail(newValue);
                    break;
                case 3: // Password
                    user.setPassword(newValue);
                    break;
                case 4: // Phone Number
                    try {
                        long newPhone = Long.parseLong(newValue);
                        user.setUserPhone(newPhone);
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid phone number format. Please enter a valid number.");
                        return; // Exit the method on error
                    }
                    break;
                case 5: // City
                    user.setCity(newValue);
                    break;
                case 6: // Pincode
                    try {
                        int newPincode = Integer.parseInt(newValue);
                        user.setPinCode(newPincode);
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid pin code format. Please enter a valid number.");
                        return; // Exit the method on error
                    }
                    break;
                default:
                    System.err.println("Invalid choice for update.");
                    return; // Exit the method on invalid choice
            }
            userDao.updateUser(user);
            System.out.println("Customer details updated successfully.");
        });
    }
}
