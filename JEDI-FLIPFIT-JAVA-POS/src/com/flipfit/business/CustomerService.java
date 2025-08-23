package com.flipfit.business;

import com.flipfit.bean.*;
import com.flipfit.dao.*;
import com.flipfit.exception.AuthenticationException;
import com.flipfit.exception.DAOException;
import com.flipfit.exception.MissingValueException;

import java.time.LocalDateTime;
import java.util.List;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;
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
        System.out.println("New slot booking request received...");

        // Throw MissingValueException for null bookingDate
        if (bookingDate == null) {
            throw new MissingValueException("Booking date cannot be null.");
        }

        Optional<GymCentre> gymCentreOptional = customerDao.getGymById(gymId);
        Optional<Slot> slotOptional = customerDao.getSlotById(slotId);
        int balance = 0;
        int bookings = 0;

        if (gymCentreOptional.isEmpty() || slotOptional.isEmpty()) {
            System.out.println("Error: Gym or slot not found.");
            return;
        }

        Optional<Integer> bookingsOptional = customerDao.getTotalBookingsBySlotIdAndBookingDate(slotId, bookingDate);

        if (bookingsOptional.isPresent()) {
            bookings = bookingsOptional.get();
        }

        try{
            balance = retrieveBalance(customerId);
        } catch(AuthenticationException e){
            System.out.println("Error: " + e.getMessage());
            return;
        }

        GymCentre gymCentre = gymCentreOptional.get();
        Slot slot = slotOptional.get();

        LocalDateTime slotDateTime = LocalDateTime.of(bookingDate, slot.getStartTime());

        if (LocalDateTime.now().isAfter(slotDateTime)) {
            System.out.println("The booking date and time have already passed. Please select a future slot.");
            return;
        }

        if(balance < gymCentre.getCost()){
            System.out.println("Insufficient balance for booking slot. Your balance: " + balance + ", Required amount: " + gymCentre.getCost() + ". Please add money to wallet.");
            return;
        }

        if(bookings >= slot.getCapacity()){
            System.out.println("This slot is already full for the the given date, please try booking some other slot or for some other date");
            return;
        }

        Booking newBooking = new Booking(
                customerId,
                gymId,
                slotId,
                "BOOKED", // Default status
                bookingDate, // Use the provided date
                LocalDateTime.now()
        );

        try{
            Optional<Integer> bookingIdOptional = customerDao.bookSlot(newBooking, gymCentre.getCost());
            if (bookingIdOptional.isPresent()) {
                System.out.println("Slot booked successfully with booking ID: " + bookingIdOptional.get());
            } else {
                System.out.println("Failed to book slot: No booking ID was generated.");
            }
        } catch(DAOException e){
            System.out.println("Error: " + e.getMessage());
        }

    }

    /**
     * Processes a customer's payment and updates their payment details.
     *
     * @param customerId The ID of the customer making the payment.
     * @param balance The balance to be added to wallet.
     */
    @Override
    public void makePayments(int customerId, int balance) {

        customerDao.makePayment(customerId, balance);
    }

    /**
     * Retrieve customer's balance based on his/her ID.
     *
     * @param customerId The customer's user ID.
     * @return An integer representing balance if the customer exists.
     * @throws AuthenticationException if the user ID is invalid.
     */
    @Override
    public Integer retrieveBalance(int customerId) {
        Optional<Integer> balanceOptional = customerDao.getBalanceById(customerId);

        if (balanceOptional.isPresent()) {

            return balanceOptional.get();
        }
        throw new AuthenticationException("Invalid customer ID.");
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
            System.out.println("Error: Customer not found.");
            return;
        }

        User user = optionalUser.get();

        // Update the correct field based on user choice
        try {
            switch (choice) {
                case 1:
                    user.setFullName(newValue);
                    break;
                case 2:
                    user.setEmail(newValue);
                    break;
                case 3:
                    user.setPassword(newValue);
                    break;
                case 4:
                    user.setUserPhone(Long.parseLong(newValue));
                    break;
                case 5:
                    user.setCity(newValue);
                    break;
                case 6:
                    user.setPinCode(Integer.parseInt(newValue));
                    break;
                default:
                    System.out.println("Invalid choice for update.");
                    return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid format for phone number or pin code. Please enter a valid number.");
            return;
        }

        // Persist the changes to the database by calling the DAO
        userDao.updateUser(user);
        System.out.println("Customer details updated successfully.");
    }

    /**
     * Updates a specific detail for a customer.
     *
     * @param userId The ID of the user (customer) whose payment details are to be updated.
     * @param paymentType Payment type to be updated.
     * @param paymentInfo Payment info to be updated.
     */
    @Override
    public void editPaymentDetails(int userId, int paymentType, String paymentInfo) {
        // Throw MissingValueException if the user is not found
        Optional<Customer> optionalCustomer = customerDao.getCustomerById(userId);
        if (optionalCustomer.isEmpty()) {
            System.out.println("Error: Customer not found.");
            return;
        }

        Customer customer = optionalCustomer.get();

        // Update the customer fields
        customer.setPaymentType(paymentType);
        customer.setPaymentInfo(paymentInfo);
        customerDao.updateCustomerDetails(customer);
        System.out.println("Customer details updated successfully.");
    }
}