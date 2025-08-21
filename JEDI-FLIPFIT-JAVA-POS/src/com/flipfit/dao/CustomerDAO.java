package com.flipfit.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CustomerDAO {

    // This DAO now focuses only on bookings.
    private static List<String[]> bookings = new ArrayList<>();

    static {
        // Hardcoded data for Bookings: {BookingID, CustomerID, SlotID, CentreID}
        bookings.add(new String[]{"B101", "1", "S201", "C301"});
        bookings.add(new String[]{"B102", "1", "S202", "C301"});
        bookings.add(new String[]{"B103", "2", "S203", "C302"});
    }

    public List<String[]> getBookingsByCustomerId(String customerId) {
        return bookings.stream()
                .filter(booking -> booking[1].equals(customerId))
                .collect(Collectors.toList());
    }

    public void addBooking(String[] bookingDetails) {
        bookings.add(bookingDetails);
    }

//    public List<String[]> getAllCustomers() {
//    }
}
