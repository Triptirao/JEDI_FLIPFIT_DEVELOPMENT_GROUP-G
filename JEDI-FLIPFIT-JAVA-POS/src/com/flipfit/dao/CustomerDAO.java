package com.flipfit.dao;

import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {

    private List<String[]> customers = new ArrayList<>();
    private List<String[]> bookings = new ArrayList<>();

    public CustomerDAO() {
        // Hardcoded data for Customers: {Role, ID, Name, Email, Password, Phone, City, Pincode}
        customers.add(new String[]{"CUSTOMER", "1", "John Doe", "john.doe@example.com", "pass123", "1234567890", "New York", "10001"});

        // Hardcoded data for Bookings: {BookingID, CustomerID, SlotID, CentreID}
        bookings.add(new String[]{"B101", "1", "S201", "C301"});
        bookings.add(new String[]{"B102", "1", "S202", "C301"});
    }

    public List<String[]> getAllCustomers() {
        return customers;
    }

    public List<String[]> getBookingsByCustomerId(String customerId) {
        List<String[]> customerBookings = new ArrayList<>();
        for (String[] booking : bookings) {
            if (booking[1].equals(customerId)) {
                customerBookings.add(booking);
            }
        }
        return customerBookings;
    }

    public void addBooking(String[] bookingDetails) {
        bookings.add(bookingDetails);
    }
}
