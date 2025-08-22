package com.flipfit.business;

import com.flipfit.bean.Booking;
import com.flipfit.bean.Customer;
import com.flipfit.bean.GymCentre;
import com.flipfit.bean.User;
import com.flipfit.dao.*;
import com.flipfit.exception.DuplicateEntryException;
import com.flipfit.exception.MismatchinputException;
import com.flipfit.exception.MissingValueException;
import java.sql.SQLException;
import java.util.List;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

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

        // Throw MissingValueException for null bookingDate
        if (bookingDate == null) {
            throw new MissingValueException("Booking date cannot be null.");
        }

        System.out.println("GymId: " + gymId + ", SlotId: " + slotId + ", BookingDate: " + bookingDate + "customerid: " + customerId);

        Booking newBooking = new Booking(
                customerId,
                gymId,
                slotId,
                "BOOKED", // Default status
                bookingDate, // Use the provided date
                LocalTime.now()
        );

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

        // Throw MissingValueException if the customer is not found
        Optional<Customer> optionalCustomer = customerDao.getCustomerById(customerId);
        if (optionalCustomer.isEmpty()) {
            throw new MissingValueException("Customer with ID " + customerId + " not found.");
        }

        Customer customer = optionalCustomer.get();
        customer.setPaymentType(paymentType);
        customer.setPaymentInfo(paymentInfo);

        customerDao.updateCustomer(customer);
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
        // Throw MissingValueException if the user is not found
        Optional<User> optionalUser = userDao.getUserById(userId);
        if (optionalUser.isEmpty()) {
            throw new MissingValueException("User with ID " + userId + " not found.");
        }

        User user = optionalUser.get();

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
                    // Throw MismatchinputException for invalid number format
                    throw new MismatchinputException("Invalid phone number format. Please enter a valid number.", e);
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
                    // Throw MismatchinputException for invalid pincode format
                    throw new MismatchinputException("Invalid pin code format. Please enter a valid number.", e);
                }
                break;
            default:
                // Throw MismatchinputException for an invalid choice
                throw new MismatchinputException("Invalid choice for update.");
        }

        userDao.updateUser(user);
        System.out.println("Customer details updated successfully.");
    }
}