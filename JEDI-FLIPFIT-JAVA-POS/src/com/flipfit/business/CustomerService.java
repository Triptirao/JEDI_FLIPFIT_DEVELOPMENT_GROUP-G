package com.flipfit.business;

import com.flipfit.bean.Booking;
import com.flipfit.dao.CustomerDAO;
import com.flipfit.dao.GymOwnerDAO;
import com.flipfit.dao.UserDAO;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class CustomerService {

    private CustomerDAO customerDao;
    private GymOwnerDAO gymOwnerDao;
    private UserDAO userDao;

    public CustomerService(CustomerDAO customerDao, UserDAO userDao, GymOwnerDAO gymOwnerDao) {
        this.customerDao = customerDao;
        this.userDao = userDao;
        this.gymOwnerDao = gymOwnerDao;
    }

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

    public List<String[]> viewCenters() {
        System.out.println("Fetching all available gym centers...");
        return gymOwnerDao.getApprovedGyms();
    }

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

    public void makePayments(int paymentType, String paymentInfo) {
        System.out.println("Processing payment of type: " + paymentType + " with account: " + paymentInfo);
    }

    public void editCustomerDetails(String customerId, int choice, String newValue) {
        userDao.updateUserDetails(customerId, choice, newValue);
        System.out.println("Customer details updated successfully.");
    }
}