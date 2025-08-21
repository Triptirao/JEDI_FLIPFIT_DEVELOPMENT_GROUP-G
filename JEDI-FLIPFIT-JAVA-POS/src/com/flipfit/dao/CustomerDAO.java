package com.flipfit.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CustomerDAO {

    // This DAO now focuses only on bookings.
    private static List<String[]> bookings = new ArrayList<>();

    static {
        // Hardcoded data for Bookings: {BookingID, CustomerID, Slot Time, CentreName}
        bookings.add(new String[]{"B101", "1", "10:00-11:00", "Panathur"});
        bookings.add(new String[]{"B102", "1", "17:00-18:00", "Kormangala"});
        bookings.add(new String[]{"B103", "2", "08:00-09:00", "Marathalli"});
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
