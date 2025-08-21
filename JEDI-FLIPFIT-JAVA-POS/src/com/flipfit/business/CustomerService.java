package com.flipfit.business;

import com.flipfit.bean.Booking;
import com.flipfit.dao.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class CustomerService {

    private CustomerDAO customerDao;
    private GymOwnerDAO gymOwnerDao;
    private UserDAO userDao;
    private CustomerService customerService;
    private GymOwnerDAO gymOwnerDAO;

    public CustomerService(CustomerDAO customerDao, UserDAO userDao, GymOwnerDAO gymOwnerDao) {
        this.customerDao = customerDao;
        this.userDao = userDao;
        this.gymOwnerDao = gymOwnerDao;
    }

    public List<Booking> viewBookedSlots(String customerId) {
        System.out.println("Fetching your booked slots...");
        try {
            // Convert the String customerId to an int
            int customerIdInt = Integer.parseInt(customerId);
            return BookingDAO.getBookingsByCustomerId(customerIdInt);
        } catch (NumberFormatException e) {
            System.err.println("Error: The customer ID must be a valid number.");
            return null; // Return null or an empty list to indicate failure
        }
    }


    public List<String[]> viewCenters() {
        System.out.println("Fetching all available gym centers...");
        return gymOwnerDAO.getApprovedGyms();
    }
    public void bookSlot(int bookingId, int customerId, int slotId, int centreId) {
        System.out.println("Booking your slot...");

        // Create a new Booking object with the provided details and set a default status.
        Booking newBooking = new Booking(
                bookingId,
                customerId,
                centreId,
                slotId,
                "BOOKED", // Default status
                LocalDate.now(),
                LocalTime.now()
        );

        // Call the static method in BookingDAO, passing the Booking object.
        BookingDAO.bookSlot(newBooking);

        System.out.println("Slot booked successfully!");

    }
    public void makePayments(int paymentType, String paymentInfo) {
        System.out.println("Processing payment of type: " + paymentType + " with account: " + paymentInfo);
    }

    public void editCustomerDetails(String customerId, int choice, String newValue) {
        userDao.updateUserDetails(customerId, choice, newValue);
        System.out.println("Customer details updated successfully.");
    }
}
