package com.flipfit.business;

import com.flipfit.bean.*;
import com.flipfit.dao.*;
import com.flipfit.exception.*;


import java.time.LocalDateTime;

import java.util.Objects;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
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
        return customerDao.getBookingsByCustomerId(customerId);
    }

    /**
     * Fetches a list of all available and approved gym centers.
     *
     * @return A list of GymCentre objects representing the available gyms.
     */
    @Override
    public List<GymCentre> viewCenters() {
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
    public void bookSlot(int customerId, int gymId, int slotId, LocalDate bookingDate) throws MissingValueException, MismatchinputException, DuplicateEntryException, AccessDeniedException {

        if (bookingDate == null) {
            throw new MissingValueException("Booking date cannot be null.");
        }

        Optional<GymCentre> gymCentreOptional = customerDao.getGymById(gymId);
        Optional<Slot> slotOptional = customerDao.getSlotById(slotId);

        if (gymCentreOptional.isPresent()) {
            if (slotOptional.isPresent()) {
                GymCentre gymCentre = gymCentreOptional.get();
                Slot slot = slotOptional.get();

                LocalDateTime slotDateTime = LocalDateTime.of(bookingDate, slot.getStartTime());
                if (LocalDateTime.now().isAfter(slotDateTime)) {
                    throw new MismatchinputException("The booking date and time have already passed. Please select a future slot.");
                }

                int currentBalance = retrieveBalance(customerId);
                if (currentBalance < gymCentre.getCost()) {
                    throw new MissingValueException("Insufficient balance for booking slot. Your balance: " + currentBalance + ", Required amount: " + gymCentre.getCost() + ". Please add money to wallet.");
                }

                int totalBookings = customerDao.getTotalBookingsBySlotIdAndBookingDate(slotId, bookingDate);
                if (totalBookings >= slot.getCapacity()) {
                    throw new DuplicateEntryException("This slot is already full for the the given date, please try booking some other slot or for some other date.");
                }

                Booking newBooking = new Booking(
                        customerId,
                        gymId,
                        slotId,
                        "BOOKED", // Default status
                        bookingDate,
                        LocalDateTime.now()
                );

                customerDao.bookSlot(newBooking, gymCentre.getCost());
            } else {
                throw new MissingValueException("Error: Slot with ID " + slotId + " not found.");
            }

        } else {
            throw new MissingValueException("Error: Gym with ID " + gymId + " not found or not approved.");
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
        Optional<Customer> optionalCustomer = customerDao.getCustomerById(customerId);
        if (optionalCustomer.isPresent()) {
            customerDao.makePayment(customerId, balance);
        } else {
            throw new MissingValueException("Customer with ID " + customerId + " not found.");
        }

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
     * Updates customer user and payment details based on provided DTOs.
     * @param userId The ID of the user (customer) whose details are to be updated.
     * @param updatedUser An object containing the new User details.
     * @param updatedCustomer An object containing the new Customer details.
     */
    @Override
    public void editCustomerDetails(int userId, User updatedUser, Customer updatedCustomer) {
        Optional<User> optionalUser = userDao.getUserById(userId);
        System.out.println(updatedUser);
        System.out.println(updatedCustomer);
        if (optionalUser.isPresent()) {
            User userToUpdate = optionalUser.get();

            Optional<Customer> optionalCustomer = customerDao.getCustomerById(userId);
            if (optionalCustomer.isPresent()) {
                Customer customerToUpdate = optionalCustomer.get();

                if (updatedUser.getFullName() != null && !updatedUser.getFullName().isEmpty()) {
                    userToUpdate.setFullName(updatedUser.getFullName());
                }
                if (updatedUser.getEmail() != null && !updatedUser.getEmail().isEmpty()) {
                    userToUpdate.setEmail(updatedUser.getEmail());
                }
                if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
                    userToUpdate.setPassword(updatedUser.getPassword());
                }
                if (updatedUser.getUserPhone() != 0) {
                    userToUpdate.setUserPhone(updatedUser.getUserPhone());
                }
                if (updatedUser.getCity() != null && !updatedUser.getCity().isEmpty()) {
                    userToUpdate.setCity(updatedUser.getCity());
                }
                if (updatedUser.getPinCode() != 0) {
                    userToUpdate.setPinCode(updatedUser.getPinCode());
                }

                if (updatedCustomer.getPaymentType() != 0) {
                    customerToUpdate.setPaymentType(updatedCustomer.getPaymentType());
                }
                if (updatedCustomer.getPaymentInfo() != null && !updatedCustomer.getPaymentInfo().isEmpty()) {
                    customerToUpdate.setPaymentInfo(updatedCustomer.getPaymentInfo());
                }

                userDao.updateUser(userToUpdate);
                customerDao.updateCustomerDetails(customerToUpdate);
            } else {
                throw new MissingValueException("Customer with ID " + userId + " not found.");
            }
        } else {
            throw new MissingValueException("User with ID " + userId + " not found.");
        }
    }

    @Override
    public void editPaymentDetails(int userId, int paymentType, String paymentInfo) {
        Optional<Customer> optionalCustomer = customerDao.getCustomerById(userId);
        if (optionalCustomer.isPresent()) {
            Customer customer = optionalCustomer.get();
            customer.setPaymentType(paymentType);
            customer.setPaymentInfo(paymentInfo);
            customerDao.updateCustomerDetails(customer);
        } else {
            throw new MissingValueException("Error: Customer not found.");
        }

    }
}
